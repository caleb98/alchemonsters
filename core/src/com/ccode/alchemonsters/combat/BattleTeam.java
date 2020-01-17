package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.DynamicVariables;

public class BattleTeam {

	public final CreatureTeam team;
	public final int numActives;
	public DynamicVariables variables = new DynamicVariables();
	
	private BattleTeam() {
		team = null;
		numActives = -1;
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
	
	public void swap(int from, int to) {
		team.swap(from, to);
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
