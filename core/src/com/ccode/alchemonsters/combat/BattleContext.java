package com.ccode.alchemonsters.combat;

import java.util.ArrayList;

import com.ccode.alchemonsters.creature.Creature;

public class BattleContext {
	
	public CombatState currentState;
	
	public BattleController teamAController;
	public CreatureTeam teamA;
	public ArrayList<BattleAction> teamAActions;
	
	public BattleController teamBController;
	public CreatureTeam teamB;
	public ArrayList<BattleAction> teamBActions;
	
	public Battleground battleground;
	
	public void update() {
		for(Creature c : teamA.creatures) {
			if(c != null && c.currentAilment != null	) {
				c.currentAilment.update();
			}
		}
		for(Creature c : teamB.creatures) {
			if(c != null && c.currentAilment != null	) {
				c.currentAilment.update();
			}
		}
	}
	
}






















