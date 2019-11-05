package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;

/**
 * Just an array of creatures with a helper functions
 * for modifying the team.
 * @author caleb
 */
public class CreatureTeam {
	
	public static final int TEAM_SIZE = 4;

	public Creature[] creatures;
	
	public CreatureTeam() {
		creatures = new Creature[TEAM_SIZE];
	}
	
	public void swap(int from, int to) {
		//TODO: error handle for out of bounds?
		Creature temp = creatures[to];
		creatures[to] = creatures[from];
		creatures[from] = temp;
	}
	
	/**
	 * @return the total number of creatures on this team. null positions do not count towards this value
	 */
	public int getNumCreatures() {
		int count = 0;
		for(int i = 0; i < creatures.length; ++i) {
			if(creatures[i] != null) count++;
		}
		return count;
	}
	
}
