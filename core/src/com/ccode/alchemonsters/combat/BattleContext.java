package com.ccode.alchemonsters.combat;

import java.util.ArrayList;

import com.ccode.alchemonsters.engine.event.Publisher;

public class BattleContext implements Publisher {
	
	public CombatState currentState;
	
	public BattleController teamAController;
	public CreatureTeam teamA;
	public ArrayList<BattleAction> teamAActions;
	
	public BattleController teamBController;
	public CreatureTeam teamB;
	public ArrayList<BattleAction> teamBActions;
	
	public Battleground battleground;
	
}






















