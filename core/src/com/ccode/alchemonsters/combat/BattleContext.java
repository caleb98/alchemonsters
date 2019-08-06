package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.VariableList;

public class BattleContext {
	
	public CombatState currentState;
	public VariableList variables = new VariableList();
	
	public BattleController teamAController;
	public CreatureTeam teamA;
	
	public BattleController teamBController;
	public CreatureTeam teamB;
	
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






















