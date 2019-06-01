package com.ccode.alchemonsters.combat;

import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import com.ccode.alchemonsters.combat.BattleAction.BattleActionType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.GameRandom;

public class BattleContext {
	
	public IBattleController teamAController;
	public CreatureTeam teamA;
	public ArrayList<BattleAction> teamAActions;
	
	public IBattleController teamBController;
	public CreatureTeam teamB;
	public ArrayList<BattleAction> teamBActions;
	
	public Battleground battleground;
	private CombatPhase currentPhase;
	
	public ArrayList<BattleEvent> startEvents;
	public ArrayList<BattleEvent> mainOneEvents;
	public ArrayList<BattleEvent> battleOneEvents;
	public ArrayList<BattleEvent> mainTwoEvents;
	public ArrayList<BattleEvent> battleTwoEvents;
	public ArrayList<BattleEvent> endEvents;
	
	public void update(float delta) {
		switch(currentPhase) {
		
		case START:
			startPhase(delta);
			break;
			
		case MAIN_1:
			mainPhaseOne(delta);
			break;
			
		case BATTLE_1:
			battlePhaseOne(delta);
			break;
		
		case MAIN_2:
			mainPhaseTwo(delta);
			break;
			
		case BATTLE_2:
			battlePhaseTwo(delta);
			break;
			
		case END:
			endPhase(delta);
			break;
		
		}
	}
	
	public void startPhase(float delta) {
		//TODO: populate available battle actions for BattleControllers

		setCombatPhase(CombatPhase.MAIN_1);
	}
	
	public void mainPhaseOne(float delta) {		
		/* TODO: timer system so that people cant
		 * take forever in multiplayer matches. */
		if(teamAController.isActionSelected() && teamBController.isActionSelected()) {
			//TODO: validate actions (and handle invalid actions appropriately)
			
			setCombatPhase(CombatPhase.BATTLE_1);
		}
	}
	
	public void battlePhaseOne(float delta) {
		BattleAction teamAAction = teamAController.getSelectedAction();
		BattleAction teamBAction = teamBController.getSelectedAction();
		
		//TODO: animated changes and waiting for that before progressing
		if(teamAAction.type == BattleActionType.SWITCH) {
			teamA.active = teamAAction.id;
		}
		if(teamBAction.type == BattleActionType.SWITCH) {
			teamB.active = teamBAction.id;
		}
		
		//TODO: item use animations?
		if(teamAAction.type == BattleActionType.USE) {
			//TODO: inventory use
		}
		if(teamBAction.type == BattleActionType.USE) {
			//TODO: inventory use
		}
		
		//TODO: combat animations
		if(teamAAction.type == BattleActionType.MOVE && teamBAction.type == BattleActionType.MOVE) {
			Creature teamAActive = teamA.active();
			Move teamAMove = teamAActive.moves[teamAAction.id];
			Creature teamBActive = teamB.active();
			Move teamBMove = teamBActive.moves[teamBAction.id];
			
			if(teamAActive.stats.speed > teamBActive.stats.speed) {
				teamAAttack(teamAActive, teamAMove);
				teamBAttack(teamBActive, teamBMove);
			}
			else if(teamAActive.stats.speed < teamBActive.stats.speed) {
				teamBAttack(teamBActive, teamBMove);
				teamAAttack(teamAActive, teamAMove);
			}
			else {
				if(GameRandom.nextBoolean()) {
					teamAAttack(teamAActive, teamAMove);
					teamBAttack(teamBActive, teamBMove);
				}
				else {
					teamBAttack(teamBActive, teamBMove);
					teamAAttack(teamAActive, teamAMove);
				}
			}
		}
		else if(teamAAction.type == BattleActionType.MOVE) {
			Creature teamAActive = teamA.active();
			Move teamAMove = teamAActive.moves[teamAAction.id];
			teamAAttack(teamAActive, teamAMove);
		}
		else if(teamBAction.type == BattleActionType.MOVE) {
			Creature teamBActive = teamB.active();
			Move teamBMove = teamBActive.moves[teamBAction.id];
			teamBAttack(teamBActive, teamBMove);
		}
		
		if(teamA.active().stats.speed >= teamB.active().stats.speed * 2) {
			//TODO: handle double hits
			//TODO: the threshold for double hits should probably be lower given the distribution of stats...
		}
		else if(teamB.active().stats.speed >= teamA.active().stats.speed * 2) {
			
		}
		else {
			setCombatPhase(CombatPhase.END);
		}
	}
	
	public void mainPhaseTwo(float delta) {
		
	}
	
	public void battlePhaseTwo(float delta) {
		
	}
	
	public void endPhase(float delta) {
		//TODO: any end phase stuff?
		
		setCombatPhase(CombatPhase.START);
	}
	
	private void teamAAttack(Creature attacker, Move move) {
		for(IMoveAction a : move.actions) {
			a.activate(move, this, attacker, teamB.active());
		}
		
		//TODO: check for death
	}
	
	private void teamBAttack(Creature attacker, Move move) {
		for(IMoveAction a : move.actions) {
			a.activate(move, this, attacker, teamA.active());
		}
		
		//TODO: check for death
	}
	
	public CombatPhase getCombatPhase() {
		return currentPhase;
	}
	
	public void setCombatPhase(CombatPhase phase) {
		switch(phase) {
		
		case BATTLE_1:
			for(BattleEvent a : battleOneEvents) {
				a.trigger(this);
			}
			break;
			
		case BATTLE_2:
			for(BattleEvent a : battleTwoEvents) {
				a.trigger(this);
			}
			break;
			
		case END:
			for(BattleEvent a : endEvents) {
				a.trigger(this);
			}
			break;
			
		case MAIN_1:
			for(BattleEvent a : mainOneEvents) {
				a.trigger(this);
			}
			break;
			
		case MAIN_2:
			for(BattleEvent a : mainTwoEvents) {
				a.trigger(this);
			}
			break;
			
		case START:
			for(BattleEvent a : startEvents) {
				a.trigger(this);
			}
			break;
			
		}
		
		currentPhase = phase;
	}
	
	public enum CombatPhase {
		START,
		MAIN_1,
		BATTLE_1,
		MAIN_2,
		BATTLE_2,
		END
	}
	
}






















