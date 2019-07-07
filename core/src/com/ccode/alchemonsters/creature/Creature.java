package com.ccode.alchemonsters.creature;

import com.ccode.alchemonsters.combat.Catalyst;
import com.ccode.alchemonsters.combat.PassiveAbility;
import com.ccode.alchemonsters.combat.StatBuffs;
import com.ccode.alchemonsters.combat.StatusAilment;

public class Creature {

	public String personalName;
	public CreatureBase base;
	public CreatureNature nature;
	private CreatureStats stats;
	public int baseHealth;
	public int baseMana;
	
	//Combat
	public int currentHealth;
	public int maxHealth;
	public int currentMana;
	public int maxMana;
	
	public StatBuffs buffs = new StatBuffs();
	public StatusAilment currentAilment;
	
	//Moves/Abilities
	public String[] moves;
	public PassiveAbility ability;
	
	//Equipment
	public Catalyst catalyst;
	
	//Level variables
	public int currentXP;
	public int nextLevelXP;
	public int currentLevel;
	
	public Creature() {
		
	}
	
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
		moves = new String[4];
		currentXP = 0;
		nextLevelXP = 1000000; //TODO: 
		currentLevel = 1;
		
		buffs = new StatBuffs();
	}
	
	/**
	 * Resets current health and mana to their 
	 * maximum values.
	 */
	public void rest() {
		currentHealth = maxHealth;
		currentMana = maxMana;
	}
	
	public boolean isDead() {
		return currentHealth <= 0;
	}
	
	public void startCombat() {
		buffs.reset();
	}
	
	public int getBaseVitae() {
		return stats.vitae;
	}
	
	public int getBaseFocus() {
		return stats.focus;
	}
	
	public int getBaseMagicAtk() {
		return stats.magicPower;
	}
	
	public int getBaseMagicDef() {
		return stats.magicResistance;
	}
	
	public int getBasePhysAtk() {
		return stats.physPenetration;
	}
	
	public int getBasePhysDef() {
		return stats.physResistance;
	}
	
	public int getBaseSpeed() {
		return stats.speed;
	}
	
	public int getCurrentVitae() {
		return stats.vitae;
	}
	
	public int getCurrentFocus() {
		return stats.focus;
	}
	
	public int getCurrentMagicAtk() {
		return (int) (stats.magicPower * buffs.getMagicPowerMultiplier());
	}
	
	public int getCurrentMagicDef() {
		return (int) (stats.magicResistance * buffs.getMagicResMultiplier());
	}
	
	public int getCurrentPhysAtk() {
		return (int) (stats.physPenetration * buffs.getPhysPowerMultiplier());
	}
	
	public int getCurrentPhysDef() {
		return (int) (stats.physResistance * buffs.getPhysResMultiplier());
	}
	
	public int getCurrentSpeed() {
		return (int) (stats.speed * buffs.getSpeedMultiplier());
	}
	
}
















