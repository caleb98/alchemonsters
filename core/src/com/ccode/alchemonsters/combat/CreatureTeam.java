package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.DynamicVariables;

public class CreatureTeam {

	public int active;
	public Creature[] creatures;
	public DynamicVariables variables = new DynamicVariables();
	
	public CreatureTeam() {
		creatures = new Creature[4];
		active = 0;
	}
	
	public Creature active() {
		return creatures[active];
	}
	
	public void startCombat() {
		variables.clear();
		for(Creature c : creatures) {
			if(c != null) {
				c.startCombat();
			}
		}
	}
	
	public boolean isDefeated() {
		for(Creature c : creatures) {
			if(c != null && !c.isDead()) {
				return false;
			}
		}
		return true;
	}
	
}
