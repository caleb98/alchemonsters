package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.DynamicVariables;

public class BattleContext {
	
	public CombatState currentState;
	public DynamicVariables variables = new DynamicVariables();
	
	public UnitController[] teamAControls;
	public BattleTeam teamA;
	
	public UnitController[] teamBControls;
	public BattleTeam teamB;
	
	public Battleground battleground;
	
	private BattleContext() {}
	
	public BattleContext(UnitController[] teamAControl, BattleTeam teamA, 
						 UnitController[] teamBControl, BattleTeam teamB) {
		
		teamAControls = teamAControl;
		this.teamA = teamA;
		teamBControls = teamBControl;
		this.teamB = teamB;
		
		for(Creature c : teamA.team.creatures) {
			if(c != null)
				c.currentContext = this;
		}
		for(Creature c : teamB.team.creatures) {
			if(c != null)
				c.currentContext = this;
		}
		
		battleground = new Battleground();
		
	}
	
	public void update() {
		for(Creature c : teamA.team.creatures) {
			if(c != null && c.currentAilment != null	) {
				c.currentAilment.update();
			}
		}
		for(Creature c : teamB.team.creatures) {
			if(c != null && c.currentAilment != null	) {
				c.currentAilment.update();
			}
		}
	}
	
	public void endCombat() {
		for(Creature c : teamA.team.creatures) {
			if(c != null)
				c.currentContext = null;
		}
		for(Creature c : teamB.team.creatures) {
			if(c != null)
				c.currentContext = null;
		}
	}
	
}






















