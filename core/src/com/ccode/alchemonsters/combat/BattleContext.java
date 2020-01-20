package com.ccode.alchemonsters.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import com.ccode.alchemonsters.combat.BattleAction.BattleActionType;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.combat.moves.MoveAction;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatStateChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;
import com.ccode.alchemonsters.util.DynamicVariables;
import com.ccode.alchemonsters.util.GameRandom;
import com.ccode.alchemonsters.util.Triple;

public class BattleContext implements Publisher {
	
	public CombatState currentState;
	public DynamicVariables variables = new DynamicVariables();
	
	public BattleTeam teamA;
	public UnitController[] teamAControls;
	public BattleTeam teamB;
	public UnitController[] teamBControls;
	
	private boolean isWaitingOnActionSelect = false;
	
	public Battleground battleground = new Battleground();
	
	public ArrayList<DelayedMoveInfo> delayedMoves = new ArrayList<>();
	
	private boolean isTeamADoubleAttack = false;
	private boolean isTeamBDoubleAttack = false;
	private int doubleAttackPosition = -1;
	
	private BattleContext() {}
	
	public BattleContext(BattleTeam teamA, UnitController[] teamAControls, BattleTeam teamB, UnitController[] teamBControls) {
		this.teamA = teamA;
		this.teamAControls = teamAControls;
		this.teamB = teamB;
		this.teamBControls = teamBControls;
	}
	
	public void startCombat() {
		publish(new MCombatStarted(this));
		setCombatState(CombatState.MAIN_PHASE_1);
	}
	
	/**
	 * Checks if the battle needs to be updated. If the battle did need to be updated
	 * and the update was carried out, this method returns true.
	 * @return true if updated; false otherwise
	 */
	public boolean updateBattle() {
		if(isWaitingOnActionSelect) {
			if(areTeamActionsSelected()) {
				isWaitingOnActionSelect = false;
				
				if(currentState == CombatState.MAIN_PHASE_1) {
					setCombatState(CombatState.BATTLE_PHASE_1);
				}
				else if(currentState == CombatState.MAIN_PHASE_2) {
					setCombatState(CombatState.BATTLE_PHASE_2);
				}
				else if(currentState == CombatState.ACTIVE_DEATH_SWAP) {
					doActiveDeathSwapPost();
					setCombatState(CombatState.MAIN_PHASE_1);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean areTeamActionsSelected() {
		for(UnitController c : teamAControls) {
			if(!c.isActionSubmitted()) return false;
		}
		for(UnitController c : teamBControls) {
			if(!c.isActionSubmitted()) return false;
		}
		return true;
	}

	/**
	 * Carries out a single battle action
	 * @param control the controller of the unit using the action
	 * @param activePos the position of the unit using the action on the team
	 * @param team the team the unit using the action is on
	 * @param other the opposite (opponent) team
	 */
	private void doBattleAction(UnitController control, int activePos, BattleTeam team, BattleTeam other) {
		if(team.get(activePos).isDead() && control.getSelectedAction().type != BattleActionType.SWITCH) {
			return;
		}
		
		BattleAction action = control.getSelectedAction();
		switch(action.type) {
		
		case MOVE:
			String moveName = team.get(activePos).moves[action.id];
			Move move = MoveDatabase.getMove(moveName);
			team.get(activePos).currentMana -= move.manaCost;
			switch(move.turnType) {
			
			case CHARGE:
				//First check for dreamscape, which makes charge moves occur instantly
				if(this.battleground.weather == WeatherType.DREAMSCAPE) {
					publish(new MCombatChargeStarted(this, team.get(activePos), other.get(action.targetPos), move));
					for(MoveAction a : move.actions) {
						a.activate(move, this, team.get(activePos), team, other.get(action.targetPos), other);
					}
					team.get(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(this, team.get(activePos), other.get(action.targetPos), move));
				}
				//If they aren't  charging already, then start the charge
				else if(!control.isCharging()) {
					control.setCharging(action.id, action.targetPos);
					publish(new MCombatChargeStarted(this, team.get(activePos), other.get(action.targetPos), move));
				}
				//Otherwise they were already charging, so execute the move
				else {
					control.stopCharging();
					for(MoveAction a : move.actions) {
						a.activate(move, this, team.get(activePos), team, other.get(action.targetPos), other);
					}
					team.get(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(this, team.get(activePos), other.get(action.targetPos), move));
				}
				break;
				
			case DELAYED:
				delayedMoves.add(new DelayedMoveInfo(team, team.get(activePos), move, other, action.targetPos, move.delayAmount));
				break;
				
			case RECHARGE:
				//Deluge makes it so that recharge abilities dont require a recharge time,
				//so only apply the recharge if the current weather is NOT deluge
				if(this.battleground.weather != WeatherType.DELUGE) {
					control.setRecharging(true);
				}
			case INSTANT:
				for(MoveAction a : move.actions) {
					switch(move.targetSelectType) {
					
					case NONE:
					case FRIENDLY_TEAM:
					case OPPONENT_TEAM:
						a.activate(move, this, team.get(activePos), team, null, other);
						break;
						
					case SELF:
					case SINGLE_FRIENDLY:
						a.activate(move, this, team.get(activePos), team, team.get(action.targetPos), other);
						break;
						
					case SINGLE_OPPONENT:
						a.activate(move, this, team.get(activePos), team, other.get(action.targetPos), other);
						break;
					
					}
				}
				team.get(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
				break;
			
			}
			break;
			
		case SWITCH:
			team.swap(activePos, action.id);
			publish(new MCombatTeamActiveChanged(this, team, action.id, activePos));
			break;
			
		case USE:
			//TODO: implement inventory and item use
			break;
			
		case WAIT:
			control.setRecharging(false);
			break;
		
		}
	}
	
	/**
	 * Generic method used to set the current combat state. Delegates action to
	 * specific functions depending on the state.
	 * @param next new combat state
	 */
	private void setCombatState(CombatState next) {
		MCombatStateChanged changed = new MCombatStateChanged(this, currentState, next);
		currentState = next;
		publish(changed);
		switch(next) {
		
		case ACTIVE_DEATH_SWAP:
			doActiveDeathSwap();
			break;
			
		case BATTLE_PHASE_1:
			doBattlePhaseOne();
			break;
			
		case BATTLE_PHASE_2:
			doBattlePhaseTwo();
			break;
			
		case END_PHASE:
			doEndPhase();
			break;
			
		case MAIN_PHASE_1:
			doMainPhaseOne();
			break;
			
		case MAIN_PHASE_2:
			doMainPhaseTwo();
			break;
			
		default:
			break;
		
		}
	}
	
	/**
	 * @return whether or not any team in this battle needs to swap any of
	 * their active mons
	 */
	private boolean checkNeedsActiveSwap() {
		for(int i = 0; i < teamA.numActives; ++i) {
			if(teamA.get(i) != null && teamA.get(i).isDead()) {
				//One of the actives is dead, make sure that
				//we have a replacement possibility.
				for(int k = teamA.numActives; k < CreatureTeam.TEAM_SIZE; ++k) {
					if(teamA.get(k) != null && !teamA.get(k).isDead()) {
						return true;
					}
				}
			}
		}
		for(int i = 0; i < teamB.numActives; ++i) {
			if(teamB.get(i) != null && teamB.get(i).isDead()) {
				//One of the actives is dead, make sure that
				//we have a replacement possibility.
				for(int k = teamB.numActives; k < CreatureTeam.TEAM_SIZE; ++k) {
					if(teamB.get(k) != null && !teamB.get(k).isDead()) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Utility function for setting default actions on multiple controls 
	 * at once.
	 * @param controls array of controls
	 * @param team team of units these controllers control
	 */
	private void setDefaultControllerActions(UnitController[] controls, BattleTeam team) {
		for(int position = 0; position < controls.length; ++position) {
			UnitController control = controls[position];
			setDefaultControllerActions(control, position, team);
		}
	}
	
	/**
	 * Utility function for setting the default actions available to a mon
	 * given the current battle state.
	 * @param control controller of the mon to set actions
	 * @param position position on the team
	 * @param team 
	 */
	private void setDefaultControllerActions(UnitController control, int position,  BattleTeam team) {
		Creature creature = team.get(position);
		if(creature == null) {
			return;
		}
		
		ArrayList<BattleAction> actions = new ArrayList<>();
		
		if(control == null) {
			System.out.println("null!");
		}
		//Check for special charging case - we'll need different actions
		if(control.isCharging()) {
			actions.add(new BattleAction(BattleActionType.MOVE, control.getChargingTargetPos(), control.getCharging()));
			control.setAllActions(actions);
			return;
		}
		
		if(control.isRecharging()) {
			//TODO: this uses -1 for target index because the "wait" action doesn't have a logical target, maybe a problem?
			actions.add(new BattleAction(BattleActionType.WAIT, -1, 0));
			control.setAllActions(actions);
			return;
		}
		
		for(int i = team.numActives; i < CreatureTeam.TEAM_SIZE; ++i) {
			if(team.get(i) != null && !team.get(i).isDead()) {
				actions.add(new BattleAction(BattleActionType.SWITCH, -1, i));
			}
		}
		
		//Loop through all the moves and add their actions
		for(int moveIndex = 0; moveIndex < creature.moves.length; ++moveIndex) {
			Move move = MoveDatabase.getMove(creature.moves[moveIndex]);
			
			BattleTeam enemyTeam;
			
			switch(move.targetSelectType) {
				
			case NONE:
			case FRIENDLY_TEAM:
			case OPPONENT_TEAM:
				actions.add(new BattleAction(BattleActionType.MOVE, -1, moveIndex));
				break;
				
			case SELF:
				actions.add(new BattleAction(BattleActionType.MOVE, position, moveIndex));
				break;
				
			case SINGLE_FRIENDLY:
				for(int teamIndex = 0; teamIndex < team.numActives; ++teamIndex) {
					if(!(team.get(teamIndex) == null) && !team.get(teamIndex).isDead()) {
						actions.add(new BattleAction(BattleActionType.MOVE, teamIndex, moveIndex));
					}
				}
				break;
				
			case SINGLE_OPPONENT:
				enemyTeam = (team == teamA) ? teamB : teamA;
				for(int teamIndex = 0; teamIndex < enemyTeam.numActives; ++teamIndex) {
					if(!(team.get(teamIndex) == null) && !enemyTeam.get(teamIndex).isDead()) {
						actions.add(new BattleAction(BattleActionType.MOVE, teamIndex, moveIndex));
					}
				}
				break;
				
			}
		}		

		actions.add(new BattleAction(BattleActionType.WAIT, -1, 0));
		
		control.setAllActions(actions);
	}
	
	//*******************************************
	// Battle Phase Methods
	// - Each of these will be called once whenever
	//   the state of the battle is changed.
	//*******************************************
	private void doMainPhaseOne() {
		//Apply damage from sandstorm
		if(battleground.weather == WeatherType.SANDSTORM) {
			
			for(int i = 0; i < teamA.numActives; ++i) {
				for(ElementType t : teamA.get(i).base.types) {
					if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
						teamA.get(i).currentHealth -= teamA.get(i).maxHealth / 16f;
						break;
					}
				}
			}
			
			for(int i = 0; i < teamB.numActives; ++i) {
				for(ElementType t : teamB.get(i).base.types) {
					if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
						teamB.get(i).currentHealth -= teamB.get(i).maxHealth / 16f;
						break;
					}
				}
			}
				
			//Check for sandstorm death
			if(checkNeedsActiveSwap()) {
				if(teamA.isDefeated()) {
					publish(new MCombatFinished(this, teamB, teamA));
					return;
				}
				else if(teamB.isDefeated()) {
					publish(new MCombatFinished(this, teamA, teamB));
					return;
				}
				else {
					setCombatState(CombatState.ACTIVE_DEATH_SWAP);
					return;
				}
			}
			
		}
		
		//Apply healing from dreamscape if applicable
		if(battleground.weather == WeatherType.DREAMSCAPE) {
			
			for(int i = 0; i < teamA.numActives; ++i) {
				teamA.get(i).currentHealth += teamA.get(i).maxHealth / 16f;
				if(teamA.get(i).currentHealth > teamA.get(i).maxHealth) 
					teamA.get(i).currentHealth = teamA.get(i).maxHealth;
			}
			
			for(int i = 0; i < teamB.numActives; ++i) {
				teamB.get(i).currentHealth += teamB.get(i).maxHealth / 16f;
				if(teamB.get(i).currentHealth > teamB.get(i).maxHealth) 
					teamB.get(i).currentHealth = teamB.get(i).maxHealth;
			}
			
		}
		
		//Apply healing from tempest if applicable
		if(battleground.weather == WeatherType.TEMPEST) {
			
			for(int i = 0; i < teamA.numActives; ++i) {
				for(ElementType t : teamA.get(i).base.types) {
					if(t == ElementType.UNDEAD) {
						teamA.get(i).currentHealth += teamA.get(i).maxHealth / 16f;
						if(teamA.get(i).currentHealth > teamA.get(i).maxHealth) 
							teamA.get(i).currentHealth = teamA.get(i).maxHealth;
						break;
					}
				}
			}
			
			for(int i = 0; i < teamB.numActives; ++i) {
				for(ElementType t : teamB.get(i).base.types) {
					if(t == ElementType.UNDEAD) {
						teamB.get(i).currentHealth += teamB.get(i).maxHealth / 16f;
						if(teamB.get(i).currentHealth > teamB.get(i).maxHealth) 
							teamB.get(i).currentHealth = teamB.get(i).maxHealth;
						break;
					}
				}
			}
			
		}
		
		isWaitingOnActionSelect = true;
		setDefaultControllerActions(teamAControls, teamA);
		setDefaultControllerActions(teamBControls, teamB);
		for(int i = 0; i < teamAControls.length; ++i) {
			teamAControls[i].refresh();
		}
		for(int i = 0; i < teamBControls.length; ++i) {
			teamBControls[i].refresh();
		}
	}
	
	private void doBattlePhaseOne() {
		//Triple contains <team of unit, active position id, the action for that unit>
		ArrayList<Triple<BattleTeam, Integer, BattleAction>> monActions = new ArrayList<>();
		for(int i = 0; i < teamA.numActives; ++i) {
			//Ignore dead/absent mons
			if(teamA.get(i) == null || teamA.get(i).isDead()) {
				continue;
			}
			monActions.add(new Triple<>(teamA, i, teamAControls[i].getSelectedAction()));
		}
		for(int i = 0; i < teamB.numActives; ++i) {
			if(teamB.get(i) == null || teamB.get(i).isDead()) {
				continue;
			}
			monActions.add(new Triple<>(teamB, i, teamBControls[i].getSelectedAction()));
		}
		
		//Sort by order in battle action enum
		monActions.sort((a, b)->{
			return a.c.compareTo(b.c);
		});
		
		//Process all switch, use, and wait actions 
		Iterator<Triple<BattleTeam, Integer, BattleAction>> iter = monActions.iterator();
		while(iter.hasNext()) {
			Triple<BattleTeam, Integer, BattleAction> info = iter.next();
			//Run all actions until we reach the move actions.
			if(info.c.type == BattleActionType.MOVE) {
				break;
			}
			else {
				doBattleAction(info.a == teamA ? teamAControls[info.b] : teamBControls[info.b], 
						       info.b, 
						       info.a, 
						       info.a == teamA ? teamB : teamA);
				iter.remove();
			}
		}
		
		//Remaining actions are move
		monActions.sort((new Comparator<Triple<BattleTeam, Integer, BattleAction>>() {

			@Override
			public int compare(Triple<BattleTeam, Integer, BattleAction> a, Triple<BattleTeam, Integer, BattleAction> b) {
				//Get the moves
				Move aMove = MoveDatabase.getMove(a.a.get(a.b).moves[a.c.id]);
				Move bMove = MoveDatabase.getMove(b.a.get(b.b).moves[b.c.id]);
				
				//Compare by priority first
				if(aMove.priority > bMove.priority) {
					return 1;
				}
				else if(aMove.priority < bMove.priority) {
					return -1;
				}
				
				//If priorities were equal, compare by speed
				Creature aMon = a.a.get(a.b);
				Creature bMon = b.a.get(b.b);
				int aSpeed = aMon.calcTotalSpeed(BattleContext.this);
				int bSpeed = bMon.calcTotalSpeed(BattleContext.this);
				
				if(aSpeed > bSpeed) {
					return -1;
				}
				else if(aSpeed < bSpeed) {
					return 1;
				}
				
				//If speeds were equal, select randomly
				return GameRandom.nextBoolean() ? -1 : 1;
			}
			
		}));
		
		//Run the rest of the actions
		//This will be all the moves
		for(Triple<BattleTeam, Integer, BattleAction> info : monActions) {
			doBattleAction(info.a == teamA ? teamAControls[info.b] : teamBControls[info.b], 
				       info.b, 
				       info.a, 
				       info.a == teamA ? teamB : teamA);
		}
		
		//Do delayed moves
		Iterator<DelayedMoveInfo> delays = delayedMoves.iterator();
		while(delays.hasNext()) {
			DelayedMoveInfo inf = delays.next();
			if(inf.delayTurns == 0) {
				Move move = inf.move;
				for(MoveAction a : move.actions) {
					a.activate(move, this, inf.sourceCreature, inf.sourceTeam, inf.targetTeam.get(inf.targetPos), inf.targetTeam);
				}
				inf.sourceCreature.variables.setVariable("_PREVIOUS_MOVE", move);
				delays.remove();
			}
			else {
				inf.delayTurns--;
			}
		}
		
		//Check for defeat
		if(teamA.isDefeated()) {
			publish(new MCombatFinished(this, teamB, teamA));
			return;
		}
		else if(teamB.isDefeated()) {
			publish(new MCombatFinished(this, teamA, teamB));
			return;
		}
		
		//Check for double attack
		//First sort by speed
		monActions.sort((new Comparator<Triple<BattleTeam, Integer, BattleAction>>() {

			@Override
			public int compare(Triple<BattleTeam, Integer, BattleAction> a, Triple<BattleTeam, Integer, BattleAction> b) {
				int aSpeed = a.a.get(a.b).calcTotalSpeed(BattleContext.this);
				int bSpeed = b.a.get(b.b).calcTotalSpeed(BattleContext.this);
				
				if(aSpeed != bSpeed) {
					return aSpeed - bSpeed;
				}
				else {
					return GameRandom.nextBoolean() ? -1 : 1;
				}
			}
			
		}).reversed()); //Reverse the list because default sorting will be by ascending
		
		if(monActions.size() == 1) {
			//Only one mon did a move, but we still want the possibility for
			//a double attack. Check this mon's speed against the highest 
			//speed of the enemy team.
			Triple<BattleTeam, Integer, BattleAction> firstInfo = monActions.get(0);
			BattleTeam otherTeam = (firstInfo.a == teamA) ? teamB : teamA;
			
			int maxSpeed = 0;
			for(int activeIndex = 0; activeIndex < otherTeam.numActives; ++activeIndex) {
				//Ignore empty slots or dead mons
				if(otherTeam.get(activeIndex) == null || otherTeam.get(activeIndex).isDead()) {
					continue;
				}
				int thisSpeed = otherTeam.get(activeIndex).calcTotalSpeed(this);
				if(thisSpeed > maxSpeed) {
					maxSpeed = thisSpeed;
				}
			}
			
			if(firstInfo.a.get(firstInfo.b).calcTotalSpeed(this) >= 1.5 * maxSpeed) {
				//There should be a double attack
				if(firstInfo.a == teamA) {
					isTeamADoubleAttack = true;
				}
				else {
					isTeamBDoubleAttack = false;
				}
				doubleAttackPosition = firstInfo.b;
				setCombatState(CombatState.MAIN_PHASE_2);
			}
		}
		else if(monActions.size() > 1) {
			//If there's a double attack, the fastest mon should have at least 1.5 the 
			//speed of the second mon
			Triple<BattleTeam, Integer, BattleAction> firstInfo = monActions.get(0);
			Triple<BattleTeam, Integer, BattleAction> secondInfo = monActions.get(1);
			int firstSpeed = firstInfo.a.get(firstInfo.b).calcTotalSpeed(this);
			int secondSpeed = secondInfo.a.get(secondInfo.b).calcTotalSpeed(this);
			
			if(firstSpeed >= 1.5 * secondSpeed) {
				Creature doubleAttackMon;
				//There should be a double attack
				if(firstInfo.a == teamA) {
					isTeamADoubleAttack = true;
					doubleAttackMon = teamA.get(firstInfo.b);
				}
				else {
					isTeamBDoubleAttack = true;
					doubleAttackMon = teamB.get(firstInfo.b);
				}
				doubleAttackPosition = firstInfo.b;
				this.variables.setVariable("_PREV_DOUBLE_ATTACK_MON", doubleAttackMon);
				setCombatState(CombatState.MAIN_PHASE_2);
				return;
			}
		}
		
		setCombatState(CombatState.END_PHASE);
	}
	
	private void doMainPhaseTwo() {
		if(isTeamADoubleAttack) {
			setDefaultControllerActions(teamAControls[doubleAttackPosition], doubleAttackPosition, teamA);
			teamAControls[doubleAttackPosition].refresh();
		}
		else if(isTeamBDoubleAttack) {
			setDefaultControllerActions(teamBControls[doubleAttackPosition], doubleAttackPosition, teamB);
			teamBControls[doubleAttackPosition].refresh();
		}
		isWaitingOnActionSelect = true;
	}
	
	private void doBattlePhaseTwo() {
		if(isTeamADoubleAttack) {
			doBattleAction(teamAControls[doubleAttackPosition], doubleAttackPosition, teamA, teamB);
		}
		else if(isTeamBDoubleAttack) {
			doBattleAction(teamBControls[doubleAttackPosition], doubleAttackPosition, teamB, teamA);
		}
		if(teamA.isDefeated()) {
			publish(new MCombatFinished(this, teamB, teamA));
			return;
		}
		else if(teamB.isDefeated()) {
			publish(new MCombatFinished(this, teamA, teamB));
			return;
		}
		else {
			setCombatState(CombatState.END_PHASE);	
		}
	}
	
	private void doEndPhase() {
		if(!checkNeedsActiveSwap()) {
			setCombatState(CombatState.MAIN_PHASE_1);
		}
		else {
			setCombatState(CombatState.ACTIVE_DEATH_SWAP);
		}
	}
	
	private void doActiveDeathSwap() {			
		for(int i = 0; i < teamA.numActives; ++i) {
			Creature creature = teamA.get(i);
			if(creature.isDead()) {
				//Look for an inactive, non-dead creature to swap to
				for(int k = teamA.numActives; k < CreatureTeam.TEAM_SIZE; ++k) {
					if(!teamA.get(k).isDead()) {
						teamAControls[i].refresh();
						teamAControls[i].filterAllActions((a)->{return a.type != BattleActionType.SWITCH;});
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < teamB.numActives; ++i) {
			Creature creature = teamB.get(i);
			if(creature.isDead()) {
				//Look for an inactive, non-dead creature to swap to
				for(int k = teamB.numActives; k < CreatureTeam.TEAM_SIZE; ++k) {
					if(!teamB.get(k).isDead()) {
						teamBControls[i].refresh();
						teamBControls[i].filterAllActions((a)->{return a.type != BattleActionType.SWITCH;});
						break;
					}
				}
			}
		}
		isWaitingOnActionSelect = true;
	}
	
	private void doActiveDeathSwapPost() {
		//Run through all swap actions
		for(int i = 0; i < teamA.numActives; ++i) {
			Creature creature = teamA.get(i);
			if(creature.isDead()) {
				//Look for an inactive, non-dead creature to swap to
				for(int k = teamA.numActives; k < CreatureTeam.TEAM_SIZE; ++k) {
					if(!teamA.get(k).isDead()) {
						doBattleAction(teamAControls[i], i, teamA, teamB);
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < teamB.numActives; ++i) {
			Creature creature = teamB.get(i);
			if(creature.isDead()) {
				for(int k = teamB.numActives; k < CreatureTeam.TEAM_SIZE; ++k) {
					if(!teamB.get(k).isDead()) {
						doBattleAction(teamBControls[i], i, teamB, teamA);
						break;
					}
				}
			}
		}
	}
	
	private class DelayedMoveInfo {
		
		BattleTeam sourceTeam;
		Creature sourceCreature;
		Move move;
		BattleTeam targetTeam;
		int targetPos;
		int delayTurns;
		
		public DelayedMoveInfo(BattleTeam team, Creature creature, Move move, BattleTeam target, int targetPos, int turns) {
			sourceTeam = team;
			sourceCreature = creature;
			this.move = move;
			targetTeam = target;
			this.targetPos = targetPos;
			delayTurns = turns;
		}
		
	}
	
}






















