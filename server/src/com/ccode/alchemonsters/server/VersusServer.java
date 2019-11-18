package com.ccode.alchemonsters.server;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleAction.BattleActionType;
import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.TeamController;
import com.ccode.alchemonsters.combat.UnitController;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.combat.moves.MoveAction;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatStateChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;
import com.ccode.alchemonsters.net.NetActionSelected;
import com.ccode.alchemonsters.net.NetErrorMessage;
import com.ccode.alchemonsters.net.NetJoinSuccess;
import com.ccode.alchemonsters.net.NetJoinVersus;
import com.ccode.alchemonsters.util.GameRandom;
import com.ccode.alchemonsters.util.Triple;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class VersusServer extends Listener implements Publisher {
	
	//Team management
	private BattleTeam teamA;
	private UnitController[] teamAControls;
	private Connection teamAConnection;
	
	private BattleTeam teamB;
	private UnitController[] teamBControls;
	private Connection teamBConnection;
	
	//Battle state
	private BattleContext context;
	private ArrayList<DelayedMoveInfo> delayedMoves	= new ArrayList<>();
	
	private boolean isWaitingOnActionSelect = false;
	
	private boolean isTeamADoubleAttack = false;
	private boolean isTeamBDoubleAttack = false;
	private int doubleAttackPosition = -1;
	
	//*******************************************
	// Combat Start - Entry to battle.
	//*******************************************
	private void startCombat() {
		//Start combat for both teams
		teamA.startCombat();
		teamB.startCombat();
		
		//Reset health and mana values
		for(Creature c : teamA.team.creatures) {
			if(c != null)
				c.rest();
		}
		for(Creature c : teamB.team.creatures) {
			if(c != null)
				c.rest();
		}
		
		//Create the battle context
		context = new BattleContext(teamA, teamB);
		
		publish(new MCombatStarted(context));
		setCombatState(CombatState.MAIN_PHASE_1);
	}
	
	//*******************************************
	// Battle Phase Methods
	// - Each of these will be called once whenever
	//   the state of the battle is changed.
	//*******************************************
	private void doBattlePhaseOne() {
		//Triple contains <team of unit, active position id, the action fctiveId(activePos), action.id);or that unit>
		ArrayList<Triple<BattleTeam, Integer, BattleAction>> monActions = new ArrayList<>();
		for(int i = 0; i < teamAControls.length; ++i) {
			//Ignore dead/absent mons
			if(teamA.get(i) == null || teamA.get(i).isDead()) {
				continue;
			}
			monActions.add(new Triple<>(teamA, i, teamAControls[i].getSelectedAction()));
		}
		for(int i = 0; i < teamBControls.length; ++i) {
			if(teamB.get(i) == null || teamB.get(i).isDead()) {
				continue;
			}
			monActions.add(new Triple<>(teamB, i, teamBControls[i].getSelectedAction()));
		}
		
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
				int aSpeed = aMon.calcTotalSpeed(context);
				int bSpeed = bMon.calcTotalSpeed(context);
				
				if(aSpeed > bSpeed) {
					return 1;
				}
				else if(aSpeed < bSpeed) {
					return -1;
				}
				
				//If speeds were equal, select randomly
				return GameRandom.nextBoolean() ? -1 : 1;
			}
			
		}).reversed());
		
		//Run the rest of the actions
		//This will be all the moves
		for(Triple<BattleTeam, Integer, BattleAction> info : monActions) {
			doBattleAction(teamAControls[info.b], info.b, info.a, info.a == teamA ? teamB : teamA);
		}
		
		//Do delayed moves
		Iterator<DelayedMoveInfo> delays = delayedMoves.iterator();
		while(delays.hasNext()) {
			DelayedMoveInfo inf = delays.next();
			if(inf.delayTurns == 0) {
				Move move = inf.move;
				for(MoveAction a : move.actions) {
					a.activate(move, context, inf.sourceCreature, inf.sourceTeam, inf.targetTeam.get(inf.targetPos), inf.targetTeam);
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
			publish(new MCombatFinished(context, teamB, teamA));
			return;
		}
		else if(teamB.isDefeated()) {
			publish(new MCombatFinished(context, teamA, teamB));
			return;
		}
		
		//Check for double attack
		//First sort by speed
		monActions.sort((new Comparator<Triple<BattleTeam, Integer, BattleAction>>() {

			@Override
			public int compare(Triple<BattleTeam, Integer, BattleAction> a, Triple<BattleTeam, Integer, BattleAction> b) {
				int aSpeed = a.a.get(a.b).calcTotalSpeed(context);
				int bSpeed = b.a.get(b.b).calcTotalSpeed(context);
				
				if(aSpeed != bSpeed) {
					return aSpeed - bSpeed;
				}
				else {
					return GameRandom.nextBoolean() ? -1 : 1;
				}
			}
			
		}).reversed());
		
		if(monActions.size() == 1) {
			//Only one mon did a move, but we still want the possibility for
			//a double attack. Check this mon's speed against the highest 
			//speed of the enemy team.
			Triple<BattleTeam, Integer, BattleAction> firstInfo = monActions.get(0);
			BattleTeam otherTeam = (firstInfo.a == teamA) ? teamB : teamA;
			
			int maxSpeed = 0;
			for(int activeIndex = 0; activeIndex < otherTeam.getNumActives(); ++activeIndex) {
				//Ignore empty slots or dead mons
				if(otherTeam.get(activeIndex) == null || otherTeam.get(activeIndex).isDead()) {
					continue;
				}
				int thisSpeed = otherTeam.get(activeIndex).calcTotalSpeed(context);
				if(thisSpeed > maxSpeed) {
					maxSpeed = thisSpeed;
				}
			}
			
			if(firstInfo.a.get(firstInfo.b).calcTotalSpeed(context) >= 1.5 * maxSpeed) {
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
			int firstSpeed = firstInfo.a.get(firstInfo.b).calcTotalSpeed(context);
			int secondSpeed = secondInfo.a.get(secondInfo.b).calcTotalSpeed(context);
			
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
				context.variables.setVariable("_PREV_DOUBLE_ATTACK_MON", doubleAttackMon);
				setCombatState(CombatState.MAIN_PHASE_2);
				return;
			}
		}
		
		setCombatState(CombatState.END_PHASE);
	}
	
	private void doBattlePhaseTwo() {
		if(isTeamADoubleAttack) {
			doBattleAction(teamAControls[doubleAttackPosition], doubleAttackPosition, teamA, teamB);
		}
		else if(isTeamBDoubleAttack) {
			doBattleAction(teamBControls[doubleAttackPosition], doubleAttackPosition, teamB, teamA);
		}
		if(teamA.isDefeated()) {
			publish(new MCombatFinished(context, teamB, teamA));
			return;
		}
		else if(teamB.isDefeated()) {
			publish(new MCombatFinished(context, teamA, teamB));
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
	
	private void doMainPhaseOne() {
		//Apply damage from sandstorm
		if(context.battleground.weather == WeatherType.SANDSTORM) {
			
			for(int i = 0; i < teamA.getNumActives(); ++i) {
				for(ElementType t : teamA.get(i).base.types) {
					if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
						teamA.get(i).currentHealth -= teamA.get(i).maxHealth / 16f;
						break;
					}
				}
			}
			
			for(int i = 0; i < teamB.getNumActives(); ++i) {
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
					publish(new MCombatFinished(context, teamB, teamA));
					return;
				}
				else if(teamB.isDefeated()) {
					publish(new MCombatFinished(context, teamA, teamB));
					return;
				}
				else {
					setCombatState(CombatState.ACTIVE_DEATH_SWAP);
					return;
				}
			}
			
		}
		
		//Apply healing from dreamscape if applicable
		if(context.battleground.weather == WeatherType.DREAMSCAPE) {
			
			for(int i = 0; i < teamA.getNumActives(); ++i) {
				teamA.get(i).currentHealth += teamA.get(i).maxHealth / 16f;
				if(teamA.get(i).currentHealth > teamA.get(i).maxHealth) 
					teamA.get(i).currentHealth = teamA.get(i).maxHealth;
			}
			
			for(int i = 0; i < teamB.getNumActives(); ++i) {
				teamB.get(i).currentHealth += teamB.get(i).maxHealth / 16f;
				if(teamB.get(i).currentHealth > teamB.get(i).maxHealth) 
					teamB.get(i).currentHealth = teamB.get(i).maxHealth;
			}
			
		}
		
		//Apply healing from tempest if applicable
		if(context.battleground.weather == WeatherType.TEMPEST) {
			
			for(int i = 0; i < teamA.getNumActives(); ++i) {
				for(ElementType t : teamA.get(i).base.types) {
					if(t == ElementType.UNDEAD) {
						teamA.get(i).currentHealth += teamA.get(i).maxHealth / 16f;
						if(teamA.get(i).currentHealth > teamA.get(i).maxHealth) 
							teamA.get(i).currentHealth = teamA.get(i).maxHealth;
						break;
					}
				}
			}
			
			for(int i = 0; i < teamB.getNumActives(); ++i) {
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
	
	private void doMainPhaseTwo() {
		if(isTeamADoubleAttack) {
			setDefaultControllerActions(teamAControls[doubleAttackPosition], doubleAttackPosition, teamA);
			teamAControls[doubleAttackPosition].refresh();
		}
		else if(isTeamBDoubleAttack) {
			setDefaultControllerActions(teamBControls[doubleAttackPosition], doubleAttackPosition, teamA);
			teamBControls[doubleAttackPosition].refresh();
		}
		isWaitingOnActionSelect = true;
	}
	
	private void doActiveDeathSwap() {			
		for(int i = 0; i < teamA.getNumActives(); ++i) {
			Creature creature = teamA.get(i);
			if(creature.isDead()) {
				teamAControls[i].refresh();
				teamAControls[i].filterAllActions((a)->{return a.type != BattleActionType.SWITCH;});
			}
		}
		
		for(int i = 0; i < teamB.getNumActives(); ++i) {
			Creature creature = teamB.get(i);
			if(creature.isDead()) {
				teamBControls[i].refresh();
				teamBControls[i].filterAllActions((a)->{return a.type != BattleActionType.SWITCH;});
			}
		}
	}
	
	//*******************************************
	// Utility Methods
	// - Methods used during phase transitions.
	//*******************************************
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
				if(context.battleground.weather == WeatherType.DREAMSCAPE) {
					publish(new MCombatChargeStarted(context, team.get(activePos), other.get(action.targetPos), move));
					for(MoveAction a : move.actions) {
						a.activate(move, context, team.get(activePos), team, other.get(action.targetPos), other);
					}
					team.get(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(context, team.get(activePos), other.get(action.targetPos), move));
				}
				//If they aren't  charging already, then start the charge
				else if(!control.isCharging()) {
					control.setCharging(action.id, action.targetPos);
					publish(new MCombatChargeStarted(context, team.get(activePos), other.get(action.targetPos), move));
				}
				//Otherwise they were already charging, so execute the move
				else {
					control.stopCharging();
					for(MoveAction a : move.actions) {
						a.activate(move, context, team.get(activePos), team, other.get(action.targetPos), other);
					}
					team.get(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(context, team.get(activePos), other.get(action.targetPos), move));
				}
				break;
				
			case DELAYED:
				delayedMoves.add(new DelayedMoveInfo(team, team.get(activePos), move, other, action.targetPos, move.delayAmount));
				break;
				
			case RECHARGE:
				//Deluge makes it so that recharge abilities dont require a recharge time,
				//so only apply the recharge if the current weather is NOT deluge
				if(context.battleground.weather != WeatherType.DELUGE) {
					control.setRecharging(true);
				}
			case INSTANT:
				for(MoveAction a : move.actions) {
					a.activate(move, context, team.get(activePos), team, other.get(action.targetPos), other);
				}
				team.get(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
				break;
			
			}
			break;
			
		case SWITCH:
			MCombatTeamActiveChanged message = new MCombatTeamActiveChanged(context, control, team, activePos, action.id);
			team.swap(activePos, action.id);
			publish(message);
			break;
			
		case USE:
			//TODO: implement inventory and item use
			break;
			
		case WAIT:
			control.setRecharging(false);
			break;
		
		}
	}
	
	private boolean checkNeedsActiveSwap() {
		for(int i = 0; i < teamA.getNumActives(); ++i) {
			if(teamA.get(i) != null && teamA.get(i).isDead()) {
				//One of the actives is dead, make sure that
				//we have a replacement possibility.
				for(int k = teamA.getNumActives(); k < CreatureTeam.TEAM_SIZE; ++k) {
					if(teamA.get(k) != null && !teamA.get(k).isDead()) {
						return true;
					}
				}
			}
		}
		for(int i = 0; i < teamB.getNumActives(); ++i) {
			if(teamB.get(i) != null && teamB.get(i).isDead()) {
				//One of the actives is dead, make sure that
				//we have a replacement possibility.
				for(int k = teamB.getNumActives(); k < CreatureTeam.TEAM_SIZE; ++k) {
					if(teamB.get(k) != null && !teamB.get(k).isDead()) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private void setDefaultControllerActions(UnitController[] controls, BattleTeam team) {
		for(int position = 0; position < controls.length; ++position) {
			UnitController control = controls[position];
			setDefaultControllerActions(control, position, team);
		}
	}
	
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
		
		for(int i = team.getNumActives(); i < CreatureTeam.TEAM_SIZE; ++i) {
			if(team.get(i) != null && !team.get(i).isDead()) {
				actions.add(new BattleAction(BattleActionType.SWITCH, -1, i));
			}
		}
		
		//Add moves targeting each of the enemies
		BattleTeam enemyTeam = (team == teamA) ? teamB : teamA;
		for(int tar = 0; tar < enemyTeam.getNumActives(); ++tar) {
			//This target is dead, so dont allow targeting it
			if(enemyTeam.get(tar) == null || enemyTeam.get(tar).isDead()) {
				continue;
			}
			for(int i = 0; i < creature.moves.length; ++i) {
				Move move = MoveDatabase.getMove(creature.moves[i]);
				if(creature.currentMana >= move.manaCost) {
					actions.add(new BattleAction(BattleActionType.MOVE, tar, i));
				}
			}
		}
		

		actions.add(new BattleAction(BattleActionType.WAIT, -1, 0));
		
		control.setAllActions(actions);
	}
	
	private void setCombatState(CombatState next) {
		MCombatStateChanged changed = new MCombatStateChanged(context, context.currentState, next);
		context.currentState = next;
		publish(changed);
		switch(next) {
		
		
		
		}
	}
	
	private void sendContextUpdate() {
		
	}
	
	//*******************************************
	// Network Methods
	//*******************************************
	@Override
	public void received(Connection connection, Object object) {
		if(object instanceof NetJoinVersus) {
			handleNetJoin(connection, (NetJoinVersus) object);
		}
		else if(object instanceof NetActionSelected) {
			handleNetActionSelected(connection, (NetActionSelected) object);
		}
	}
	
	private void handleNetActionSelected(Connection connection, NetActionSelected select) {
		if(connection == teamAConnection) {
			teamAControls[select.activePos].setSelectedAction(select.actionSelected);
		}
		else if(connection == teamBConnection) {
			
		}
	}
	
	private void handleNetJoin(Connection connection, NetJoinVersus join) {
		//Make sure there's space for the new player
		if(teamAConnection != null && teamBConnection != null) {
			connection.sendTCP(new NetErrorMessage("Error: Lobby full.", NetErrorMessage.ERR_LOBBY_FULL));
			System.out.printf("Refusing new connection from %s. Reason: Lobby full.\n", connection.getRemoteAddressTCP());
			return;
		}
		
		//See where the player should be added
		if(teamAConnection == null) {
			teamAConnection = connection;
			teamA = join.team;
			
			NetworkedTeamController controls = new NetworkedTeamController(connection, join.numActives);
			teamAControls = controls.getControls();
			connection.sendTCP(new NetJoinSuccess());
		}
		else if(teamBConnection == null) {
			teamBConnection = connection;
			teamB = join.team;
			
			NetworkedTeamController controls = new NetworkedTeamController(connection, join.numActives);
			teamBControls = controls.getControls();
			connection.sendTCP(new NetJoinSuccess());
		}
		else {
			//TODO: this should never happen
			connection.sendTCP(new NetErrorMessage("Error: Unable to join lobby. Slots available but no null connections.",
												   NetErrorMessage.ERR_JOIN_ERROR));
			return;
		}
		
		System.out.printf(
			"Client %s joined!\n\tTeam:\t1. %s\n\t\t2. %s\n\t\t3. %s\n\t\t4. %s\n",
			connection.getRemoteAddressTCP(),
			join.team.get(0) == null ? "<empty>" : join.team.get(0).personalName,
			join.team.get(1) == null ? "<empty>" : join.team.get(1).personalName,
			join.team.get(2) == null ? "<empty>" : join.team.get(2).personalName,
			join.team.get(3) == null ? "<empty>" : join.team.get(3).personalName
		);
		
		//Check to see if the combat should be started.
		if(teamAConnection != null && teamBConnection != null) {
			System.out.println("Two players connected. Starting combat!");
			startCombat();
		}
	}
	
	@Override
	public void disconnected(Connection connection) {
		//TODO: stop battle and reset
		if(connection == teamAConnection) {
			
		}
		else if(connection == teamBConnection) {
			
		}
	}
	
	@Override
	public void publish(Message message) {
		Publisher.super.publish(message);
		if(teamAConnection != null && teamAConnection.isConnected()) {
			teamAConnection.sendTCP(message);
		}
		if(teamBConnection != null && teamBConnection.isConnected()) {
			teamBConnection.sendTCP(message);
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