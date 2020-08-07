package com.ccode.alchemonsters.combat.context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleAction.BattleActionType;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.Battleground;
import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.UnitController;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.effect.Ailment;
import com.ccode.alchemonsters.combat.effect.Effect;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.combat.moves.MoveAction;
import com.ccode.alchemonsters.combat.moves.MoveInstance;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatMovePostCast;
import com.ccode.alchemonsters.engine.event.messages.MCombatMovePreCast;
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
	
	private boolean isWaitingOnBattleEventProcessing = false;
	private CombatState nextState;
	
	public Battleground battleground = new Battleground();
	
	public ArrayList<DelayedMoveInfo> delayedMoves = new ArrayList<>();
	
	private LinkedList<BattleEvent> unprocessed = new LinkedList<>();
	
	private boolean isTeamADoubleAttack = false;
	private boolean isTeamBDoubleAttack = false;
	private int doubleAttackPosition = -1;
	
	@SuppressWarnings("unused")
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
	
	public void addBattleEvent(BattleEvent e) {
		unprocessed.add(e);
	}
	
	/**
	 * Checks if the battle needs to be updated. If the battle did need to be updated
	 * and the update was carried out, this method returns true.
	 * @return true if updated; false otherwise
	 */
	public boolean updateBattle() {
		//Check for unprocessed BattleEvents and run them if they exist
		if(isWaitingOnBattleEventProcessing) {
			
			int cycle = 0;
			while(unprocessed.size() > 0) {
				
				BattleEvent next = unprocessed.pop();
				next.runEvent(this);
				unprocessed.sort(new Comparator<BattleEvent>() {
					public int compare(BattleEvent a, BattleEvent b) {
						return b.getSpeed() - a.getSpeed(); //Do this backwards to we sort from highest to lowest
					}
				});
				
				System.out.println(teamA.get(1).mods.getSpeedMultiplier());
				
			}
			
			isWaitingOnBattleEventProcessing = false;
			
			//Check for defeat
			if(teamA.isDefeated()) {
				publish(new MCombatFinished(this, teamB, teamA));
			}
			else if(teamB.isDefeated()) {
				publish(new MCombatFinished(this, teamA, teamB));
			}
			else if(needsActiveSwap()) {
				setCombatState(CombatState.ACTIVE_DEATH_SWAP);
			}
			else {
				setCombatState(nextState);
			}
			
			return true;
			
		}
		else if(isWaitingOnActionSelect) {
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
					setCombatState(nextState);
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
	 * @param sourceTeam the team the unit using the action is on
	 * @param opponentTeam the opposite (opponent) team
	 */
	protected void doBattleAction(UnitController control, int activePos, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		if(sourceTeam.get(activePos).isDead() && control.getSelectedAction().type != BattleActionType.SWITCH) {
			return;
		}
		
		BattleAction action = control.getSelectedAction();
		switch(action.type) {
		
		case MOVE:
			//Grab move
			String moveName = sourceTeam.get(activePos).moves[action.id];
			Move move = MoveDatabase.getMove(moveName);
			
			//Create move instance
			Creature[] targets;
			switch(move.targetSelectType) {
			
			case ALL:
				targets = new Creature[sourceTeam.numActives + opponentTeam.numActives];
				int insert = 0;
				for(int i = 0; i < sourceTeam.numActives; ++i) {
					targets[insert++] = sourceTeam.get(i);
				}
				for(int i = 0; i < opponentTeam.numActives; ++i) {
					targets[insert++] = opponentTeam.get(i);
				}
				break;
				
			case FRIENDLY_TEAM:
				targets = new Creature[sourceTeam.numActives];
				for(int i = 0; i < sourceTeam.numActives; ++i) {
					targets[i] = sourceTeam.get(i);
				}
				break;
				
			case NONE:
				targets = new Creature[]{};
				break;
				
			case OPPONENT_TEAM:
				targets = new Creature[opponentTeam.numActives];
				for(int i = 0; i < opponentTeam.numActives; ++i) {
					targets[i] = opponentTeam.get(i);
				}
				break;
				
			case SELF:
				targets = new Creature[]{sourceTeam.get(activePos)};
				break;
				
			case SINGLE_ANY:
				if(action.isTargetingEnemy) {
					targets = new Creature[]{opponentTeam.get(action.targets[0])};
				}
				else {
					targets = new Creature[]{sourceTeam.get(action.targets[0])};
				}
				break;
				
			case SINGLE_FRIENDLY:
				targets = new Creature[]{sourceTeam.get(action.targets[0])};
				break;
				
			case SINGLE_OPPONENT:
				targets = new Creature[]{opponentTeam.get(action.targets[0])};
				break;
				
			default:
				targets = null;
				break;
			
			}
			
			MoveInstance moveInstance = new MoveInstance(move, sourceTeam.get(activePos), targets, this);
		
			//Go ahead and subtract the mana cost
			//FIXME: UNCOMMENT THIS LINE!!!!!!
			//sourceTeam.get(activePos).modifyMana(-move.manaCost);
			
			//Carry out the move depending on its turn type
			switch(move.turnType) {
			
			case CHARGE:
				//First check for dreamscape, which makes charge moves occur instantly
				if(this.battleground.weather == WeatherType.DREAMSCAPE && !control.isCharging()) {
					
					publish(new MCombatChargeStarted(moveInstance));
					executeMove(moveInstance, sourceTeam, opponentTeam);
					publish(new MCombatChargeFinished(moveInstance));
					
				}
				
				//If they aren't  charging already, then start the charge
				else if(!control.isCharging()) {
					control.setCharging(action.id, action.targets, action.isTargetingEnemy);
					publish(new MCombatChargeStarted(moveInstance));
				}
				
				//Otherwise they were already charging, so execute the move
				else {
					
					control.stopCharging();
					executeMove(moveInstance, sourceTeam, opponentTeam);
					publish(new MCombatChargeFinished(moveInstance));
					
				}
				break;
				
			case DELAYED:
				delayedMoves.add(new DelayedMoveInfo(sourceTeam, sourceTeam.get(activePos), move, opponentTeam, action.targets, move.delayAmount));
				break;
				
			case RECHARGE:
				//Deluge makes it so that recharge abilities dont require a recharge time,
				//so only apply the recharge if the current weather is NOT deluge
				if(this.battleground.weather != WeatherType.DELUGE) {
					control.setRecharging(true);
				}
			case INSTANT:
				executeMove(moveInstance, sourceTeam, opponentTeam);
				break;
			
			}
			
			//After the move is complete, check to see if this unit has a double attack for
			//this round. If so, make sure that the target of this move was not killed, as
			//killing an enemy removes the chance for a second attack.
			if((sourceTeam == teamA && isTeamADoubleAttack) || (sourceTeam == teamB && isTeamBDoubleAttack)) {
				if(activePos == doubleAttackPosition) {
					for(Creature t : targets) {
						if(t.isDead()) {
							isTeamADoubleAttack = false;
							isTeamBDoubleAttack = false;
							nextState = CombatState.END_PHASE;
							break;
						}
					}
				}
			}
			break;
			
		case SWITCH:
			sourceTeam.swap(activePos, action.id);
			publish(new MCombatTeamActiveChanged(this, sourceTeam, action.id, activePos));
			break;
			
		case USE:
			//TODO: implement inventory and item use
			break;
			
		case WAIT:
			control.setRecharging(false);
			break;
		
		}
	}
	
	private void executeMove(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		publish(new MCombatMovePreCast(moveInstance));
		
		for(MoveAction a : moveInstance.move.actions) {
			a.activate(moveInstance, sourceTeam, opponentTeam);
		}
		
		publish(new MCombatMovePostCast(moveInstance));
		
		moveInstance.source.variables.setVariable("_PREVIOUS_MOVE", moveInstance);
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
		
		//Loop through the effects on creatures and update them. Check if they need to be removed
		for(Creature c : teamA.creatures()) {
			if(c != null) {
				
				//Update Effects
				Iterator<Effect> effectIter = c.activeEffects.iterator();
				while(effectIter.hasNext()) {
					Effect e = effectIter.next();
					e.enterState(this, next);
					if(e.needsRemoval()) {
						e.onRemove(c);
						effectIter.remove();
					}
				}
				
				//Update Ailments
				Iterator<Ailment> ailmentIter = c.activeAilments.iterator();
				while(ailmentIter.hasNext()) {
					Ailment a = ailmentIter.next();
					a.enterState(this, next);
					if(a.needsRemoval()) {
						a.onRemove(c);
						ailmentIter.remove();
					}
				}
				
				//Check strong ailment
				if(c.strongAilment != null) {
					c.strongAilment.enterState(this, next);
					if(c.strongAilment.needsRemoval()) {
						c.strongAilment.onRemove(c);
						c.strongAilment = null;
					}
				}
				
			}
		}
		
		for(Creature c : teamB.creatures()) {
			if(c != null) {
				
				//Update Effects
				Iterator<Effect> effectIter = c.activeEffects.iterator();
				while(effectIter.hasNext()) {
					Effect e = effectIter.next();
					e.enterState(this, next);
					if(e.needsRemoval()) {
						e.onRemove(c);
						effectIter.remove();
					}
				}
				
				//Update Ailments
				Iterator<Ailment> ailmentIter = c.activeAilments.iterator();
				while(ailmentIter.hasNext()) {
					Ailment a = ailmentIter.next();
					a.enterState(this, next);
					if(a.needsRemoval()) {
						a.onRemove(c);
						ailmentIter.remove();
					}
				}
				
				//Check strong ailment
				if(c.strongAilment != null) {
					c.strongAilment.enterState(this, next);
					if(c.strongAilment.needsRemoval()) {
						c.strongAilment.onRemove(c);
						c.strongAilment = null;
					}
				}
				
			}
		}
		
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
	private boolean needsActiveSwap() {
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
			actions.add(new BattleAction(BattleActionType.MOVE, control.getCharging(), control.isChargeTargetingEnemy(), control.getChargingTargetPos()));
			control.setAllActions(actions);
			return;
		}
		
		if(control.isRecharging()) {
			//TODO: this uses -1 for target index because the "wait" action doesn't have a logical target, maybe a problem?
			actions.add(new BattleAction(BattleActionType.WAIT, 0, false));
			control.setAllActions(actions);
			return;
		}
		
		for(int i = team.numActives; i < CreatureTeam.TEAM_SIZE; ++i) {
			if(team.get(i) != null && !team.get(i).isDead()) {
				actions.add(new BattleAction(BattleActionType.SWITCH, i, false));
			}
		}
		
		//Loop through all the moves and add their actions
		for(int moveIndex = 0; moveIndex < creature.moves.length; ++moveIndex) {
			Move move = MoveDatabase.getMove(creature.moves[moveIndex]);
			
			BattleTeam enemyTeam = (team == teamA) ? teamB : teamA;
			
			switch(move.targetSelectType) {
				
			//These moves target more than one mon, so they don't have a logical "single" target
			//that can be selected.
			case NONE:
			case ALL:
			case FRIENDLY_TEAM:
			case OPPONENT_TEAM:
				actions.add(new BattleAction(BattleActionType.MOVE, moveIndex, false, new int[]{}));
				break;
				
			case SELF:
				actions.add(new BattleAction(BattleActionType.MOVE, moveIndex, false, position));
				break;
				
			case SINGLE_FRIENDLY:
				for(int teamIndex = 0; teamIndex < team.numActives; ++teamIndex) {
					if(!(team.get(teamIndex) == null) && !team.get(teamIndex).isDead()) {
						actions.add(new BattleAction(BattleActionType.MOVE, moveIndex, false, teamIndex));
					}
				}
				break;
				
			case SINGLE_OPPONENT:
				for(int teamIndex = 0; teamIndex < enemyTeam.numActives; ++teamIndex) {
					if(!(enemyTeam.get(teamIndex) == null) && !enemyTeam.get(teamIndex).isDead()) {
						actions.add(new BattleAction(BattleActionType.MOVE, moveIndex, true, teamIndex));
					}
				}
				break;
				
			case SINGLE_ANY:
				for(int teamIndex = 0; teamIndex < team.numActives; ++teamIndex) {
					if(!(team.get(teamIndex) == null) && !team.get(teamIndex).isDead()) {
						actions.add(new BattleAction(BattleActionType.MOVE, moveIndex, false, teamIndex));
					}
				}
				for(int teamIndex = 0; teamIndex < enemyTeam.numActives; ++teamIndex) {
					if(!(enemyTeam.get(teamIndex) == null) && !enemyTeam.get(teamIndex).isDead()) {
						actions.add(new BattleAction(BattleActionType.MOVE, moveIndex, true, teamIndex));
					}
				}
				break;
				
			}
		}		

		actions.add(new BattleAction(BattleActionType.WAIT, 0, false));
		
		control.setAllActions(actions);
	}
	
	//*******************************************
	// Battle Phase Methods
	// - Each of these will be called once whenever
	//   the state of the battle is changed.
	//*******************************************
	private void doMainPhaseOne() {		
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
		
		//Add all switch, use, and wait actions 
		Iterator<Triple<BattleTeam, Integer, BattleAction>> iter = monActions.iterator();
		int movePos = Integer.MAX_VALUE;
		while(iter.hasNext()) {
			Triple<BattleTeam, Integer, BattleAction> info = iter.next();
			//Run all actions until we reach the move actions.
			if(info.c.type == BattleActionType.MOVE) {
				break;
			}
			else {
				final int thisMovePos = movePos;
				unprocessed.add(new BattleEventAction(
						info.a == teamA ? teamAControls[info.b] : teamBControls[info.b],
						info.b,
						info.a,
						info.a == teamA ? teamB : teamA,
						()->{return thisMovePos;}));
				iter.remove();
				movePos--;
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
			unprocessed.add(new BattleEventAction(
					info.a == teamA ? teamAControls[info.b] : teamBControls[info.b],
					info.b,
					info.a,
					info.a == teamA ? teamB : teamA,
					()->{return info.a.get(info.b).calcTotalSpeed(this);}));
		}
		
		//Do delayed moves
		Iterator<DelayedMoveInfo> delays = delayedMoves.iterator();
		while(delays.hasNext()) {
			DelayedMoveInfo inf = delays.next();
			if(inf.delayTurns == 0) {
				
				//Grab move
				Move move = inf.move;
				
				//Create move instance
				Creature[] targets = new Creature[inf.targets.length];
				for(int i = 0; i < targets.length; ++i) {
					targets[i] = inf.opponentTeam.get(inf.targets[i]);
				}
				MoveInstance moveInstance = new MoveInstance(move, inf.sourceCreature, targets, this);
				
				//Do the action
				executeMove(moveInstance, inf.sourceTeam, inf.opponentTeam);
				delays.remove();
				
			}
			else {
				inf.delayTurns--;
			}
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
			
			int maxSpeed = -1;
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
					isTeamBDoubleAttack = true;
				}
				doubleAttackPosition = firstInfo.b;

				isWaitingOnBattleEventProcessing = true;
				nextState = CombatState.MAIN_PHASE_2;
				return;
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
				//There should be a double attack
				if(firstInfo.a == teamA) {
					isTeamADoubleAttack = true;
				}
				else {
					isTeamBDoubleAttack = true;
				}
				doubleAttackPosition = firstInfo.b;

				isWaitingOnBattleEventProcessing = true;
				nextState = CombatState.MAIN_PHASE_2;
				return;
			}
		}
		
		isWaitingOnBattleEventProcessing = true;
		nextState = CombatState.END_PHASE;
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
			unprocessed.add(new BattleEventAction(teamAControls[doubleAttackPosition], doubleAttackPosition, teamA, teamB, ()->{return teamA.get(doubleAttackPosition).calcTotalSpeed(this);}));
			
		}
		else if(isTeamBDoubleAttack) {
			unprocessed.add(new BattleEventAction(teamBControls[doubleAttackPosition], doubleAttackPosition, teamB, teamA, ()->{return teamB.get(doubleAttackPosition).calcTotalSpeed(this);}));
		}
		
		isWaitingOnBattleEventProcessing = true;
		nextState = CombatState.END_PHASE;
	}
	
	private void doEndPhase() {
		//Apply damage from sandstorm
		if(battleground.weather == WeatherType.SANDSTORM) {
			
			for(int i = 0; i < teamA.numActives; ++i) {
				for(ElementType t : teamA.get(i).base.types) {
					if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
						unprocessed.add(new BattleEventDamage(
								null,
								"Sandstorm", 
								teamA.get(i),
								null,
								(int) (teamA.get(i).maxHealth / 16f),
								false,
								false,
								false));
						break;
					}
				}
			}
			
			for(int i = 0; i < teamB.numActives; ++i) {
				for(ElementType t : teamB.get(i).base.types) {
					if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
						unprocessed.add(new BattleEventDamage(
								null,
								"Sandstorm", 
								teamB.get(i),
								null,
								(int) (teamB.get(i).maxHealth / 16f),
								false,
								false,
								false));						
						break;
					}
				}
			}
			
		}
		
		//Apply healing from dreamscape if applicable
		if(battleground.weather == WeatherType.DREAMSCAPE) {
			
			for(int i = 0; i < teamA.numActives; ++i) {
				unprocessed.add(new BattleEventHealing("Dreamscape", teamA.get(i), (int) (teamA.get(i).maxHealth / 16f), false));
			}
			
			for(int i = 0; i < teamB.numActives; ++i) {
				unprocessed.add(new BattleEventHealing("Dreamscape", teamB.get(i), (int) (teamB.get(i).maxHealth / 16f), false));
			}
			
		}
		
		//Apply healing from tempest if applicable
		if(battleground.weather == WeatherType.TEMPEST) {
			
			for(int i = 0; i < teamA.numActives; ++i) {
				for(ElementType t : teamA.get(i).base.types) {
					if(t == ElementType.UNDEAD) {
						unprocessed.add(new BattleEventHealing("Tempest", teamA.get(i), (int) (teamA.get(i).maxHealth / 16f), false));
						break;
					}
				}
			}
			
			for(int i = 0; i < teamB.numActives; ++i) {
				for(ElementType t : teamB.get(i).base.types) {
					if(t == ElementType.UNDEAD) {
						unprocessed.add(new BattleEventHealing("Tempest", teamB.get(i), (int) (teamB.get(i).maxHealth / 16f), false));
						break;
					}
				}
			}
			
		}
		
		isWaitingOnBattleEventProcessing = true;
		nextState = CombatState.MAIN_PHASE_1;
	}
	
	private void doActiveDeathSwap() {			
		for(int i = 0; i < teamA.numActives; ++i) {
			Creature creature = teamA.get(i);
			if(creature.isDead()) {
				//Look for an inactive, non-dead creature to swap to
				for(int k = teamA.numActives; k < CreatureTeam.TEAM_SIZE; ++k) {
					if(!teamA.get(k).isDead()) {
						teamAControls[i].stopCharging();
						teamAControls[i].setRecharging(false);
						teamAControls[i].refresh();
						
						//We have to reset the default controller actions here
						//because the controller may have been charging, which
						//would result in no switch actions being available
						//(as you can only choose to continue charging). Since
						//we call stopCharging() and setRecharging(false) right
						//before this, we know we'll have all the switch actions
						//available to filter down to.
						setDefaultControllerActions(teamAControls[i], i, teamA);
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
						teamBControls[i].stopCharging();
						teamBControls[i].setRecharging(false);
						teamBControls[i].refresh();
						
						//We have to reset the default controller actions here
						//because the controller may have been charging, which
						//would result in no switch actions being available
						//(as you can only choose to continue charging). Since
						//we call stopCharging() and setRecharging(false) right
						//before this, we know we'll have all the switch actions
						//available to filter down to.
						setDefaultControllerActions(teamBControls[i], i, teamB);
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
		BattleTeam opponentTeam;
		int[] targets;
		int delayTurns;
		
		public DelayedMoveInfo(BattleTeam team, Creature creature, Move move, BattleTeam target, int[] targets, int turns) {
			sourceTeam = team;
			sourceCreature = creature;
			this.move = move;
			opponentTeam = target;
			this.targets = targets;
			delayTurns = turns;
		}
		
	}
	
}






















