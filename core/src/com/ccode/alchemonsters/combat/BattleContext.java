package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.DynamicVariables;

public class BattleContext {
	
	public CombatState currentState;
	public DynamicVariables variables = new DynamicVariables();
	
	public BattleController teamAController;
	public CreatureTeam teamA;
	
	public BattleController teamBController;
	public CreatureTeam teamB;
	
	public Battleground battleground;
	
	public BattleContext(BattleController teamAControl, CreatureTeam teamA, 
						 BattleController teamBControl, CreatureTeam teamB) {
		
		teamAController = teamAControl;
		this.teamA = teamA;
		teamBController = teamBControl;
		this.teamB = teamB;
		
		for(Creature c : teamA.creatures) {
			c.currentContext = this;
		}
		for(Creature c : teamB.creatures) {
			c.currentContext = this;
		}
		
		battleground = new Battleground();
		
	}
	
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
	
	public void endCombat() {
		for(Creature c : teamA.creatures) {
			c.currentContext = null;
		}
		for(Creature c : teamB.creatures) {
			c.currentContext = null;
		}
	}
	
}






















