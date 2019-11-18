package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.DynamicVariables;

public class BattleContext {
	
	public CombatState currentState;
	public DynamicVariables variables = new DynamicVariables();
	
	public BattleTeam teamA;
	public BattleTeam teamB;
	
	public Battleground battleground;
	
	private BattleContext() {}
	
	public BattleContext(BattleTeam teamA, BattleTeam teamB) {
		this.teamA = teamA;
		this.teamB = teamB;
		
		battleground = new Battleground();
	}
	
	public void update() {
		for(Creature c : teamA.team.creatures) {
			if(c != null && c.currentAilment != null) {
				c.currentAilment.update();
			}
		}
		for(Creature c : teamB.team.creatures) {
			if(c != null && c.currentAilment != null) {
				c.currentAilment.update();
			}
		}
	}
	
}






















