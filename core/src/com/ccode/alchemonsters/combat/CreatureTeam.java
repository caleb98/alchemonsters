package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;

public class CreatureTeam {

	public Creature[] creatures;
	
	public CreatureTeam() {
		creatures = new Creature[4];
	}
	
	public int getNumCreatures() {
		int count = 0;
		for(int i = 0; i < creatures.length; ++i) {
			if(creatures[i] != null) count++;
		}
		return count;
	}
	
}
