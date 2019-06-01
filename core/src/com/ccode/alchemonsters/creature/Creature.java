package com.ccode.alchemonsters.creature;

import com.ccode.alchemonsters.combat.Catalyst;
import com.ccode.alchemonsters.combat.Move;
import com.ccode.alchemonsters.combat.PassiveAbility;
import com.ccode.alchemonsters.combat.StatBuffs;
import com.ccode.alchemonsters.combat.StatusAilment;

public class Creature {

	public String personalName;
	public CreatureBase base;
	public CreatureNature nature;
	public CreatureStats stats;
	
	//Combat
	public int currentHealth;
	public int maxHealth;
	public int currentMana;
	public int maxMana;
	
	public StatBuffs buffs;
	public StatusAilment currentAilment;
	
	//Moves/Abilities
	public Move[] moves;
	public PassiveAbility ability;
	
	//Equipment
	public Catalyst catalyst;
	
	//Level variables
	public int currentXP;
	public int nextLevelXP;
	public int currentLevel;
	
	/**
	 * Creates a new level one creature from the given base with the given nature
	 * and stats.
	 * @param base
	 * @param nature
	 * @param stats
	 */
	public Creature(CreatureBase base, CreatureNature nature, CreatureStats stats) {
		this.base = base;
		this.nature = nature;
		this.stats = stats;
		
		//TODO: remove, this is test only (include actual health calculation)
		currentHealth = 50;
		maxHealth = 50;
		currentMana = 50;
		maxMana = 50;
		
		personalName = base.name;
		moves = new Move[4];
		currentXP = 0;
		nextLevelXP = 1000000; //TODO: 
		currentLevel = 1;
		
		buffs = new StatBuffs();
	}
	
	public void enterCombat() {
		buffs = new StatBuffs();
	}
	
	public void exitCombat() {
		
	}
	
}
