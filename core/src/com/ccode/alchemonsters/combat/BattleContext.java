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
	
}






















