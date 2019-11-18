package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.DynamicVariables;

public class BattleTeam {

	public final CreatureTeam team;
	public int numActives;
	public DynamicVariables variables = new DynamicVariables();
	
	private BattleTeam() {
		team = null;
	}
	
	public BattleTeam(CreatureTeam team, int activePositions) {
		this.team = team;
		numActives = activePositions;
	}
	
	public Creature get(int index) {
		return team.creatures[index];
	}
	
	public Creature[] creatures() {
		return team.creatures;
	}
	
	
	public int getNumActives() {
		return numActives;
	}
	
	public void swap(int from, int to) {
		team.swap(from, to);
	}
	
	public void startCombat() {
		variables.clear();
		for(Creature c : team.creatures) {
			if(c != null) {
				c.startCombat();
			}
		}
	}
	
	public boolean isDefeated() {
		for(Creature c : team.creatures) {
			if(c != null && !c.isDead()) {
				return false;
			}
		}
		return true;
	}
	
}
