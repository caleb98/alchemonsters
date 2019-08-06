package com.ccode.alchemonsters.creature;

import com.ccode.alchemonsters.combat.Catalyst;
import com.ccode.alchemonsters.combat.PassiveAbility;
import com.ccode.alchemonsters.combat.StatBuffs;
import com.ccode.alchemonsters.combat.ailments.StatusAilment;
import com.ccode.alchemonsters.util.VariableList;

public class Creature {

	private static final int LEVEL_ONE_STAT_VALUE = 5;
	
	public String personalName;
	public CreatureBase base;
	public CreatureNature nature;
	private CreatureStats attunementValues;
	private CreatureStats experienceValues;
	public int baseHealth;
	public int baseMana;
	
	//Combat
	public int currentHealth;
	public int maxHealth;
	public int currentMana;
	public int maxMana;
	public VariableList variables = new VariableList();
	
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
	 * @param attunementValues
	 */
	public Creature(CreatureBase base, CreatureNature nature, CreatureStats attunementValues, int baseHealth, int baseMana) {
		this.base = base;
		this.nature = nature;
		this.attunementValues = attunementValues;
		this.baseHealth = baseHealth;
		this.baseMana = baseMana;
		
		experienceValues = new CreatureStats(0, 0, 0, 0, 0, 0, 0, 0, 0);
		
		personalName = base.name;
		moves = new String[4];
		currentXP = 0;
		nextLevelXP = 1000000; //TODO: experience gain and leveling up
		currentLevel = 1;
		
		calcDerivedStats();
	}
	
	public void calcDerivedStats() {
		int healthDif = base.maxBaseHealth - base.minBaseHealth;
		maxHealth = (int) (baseHealth + 
				           (currentLevel * (healthDif / 10f)) + 
				           (currentLevel * (base.baseVitae / 60f)) +
				           (currentLevel * (attunementValues.vitae / 15f)) +
				           (currentLevel * (experienceValues.vitae / 60f)));
		currentHealth = maxHealth;
		
		int manaDif = base.maxBaseMana - base.minBaseMana;
		maxMana = (int) (baseMana + 
				         (currentLevel * (manaDif / 5f)) + 
				         (currentLevel * (base.baseFocus / 60f)) +
				         (currentLevel * (attunementValues.focus / 15f)) +
				         (currentLevel * (experienceValues.vitae / 60f)));
		currentMana = maxMana;
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
		variables.clear();
	}
	
	public int getBuffedMagicAtk() {
		return (int) ((LEVEL_ONE_STAT_VALUE + 
				      (base.baseMagicAtk - LEVEL_ONE_STAT_VALUE) * (currentLevel / 100f) + 
				      (currentLevel / 100f) * attunementValues.magicAtk +
				      (currentLevel / 100f) * experienceValues.magicAtk)
					  * buffs.getMagicAtkMultiplier());
	}
	
	public int getBuffedMagicDef() {
		return (int) ((LEVEL_ONE_STAT_VALUE + 
				      (base.baseMagicDef - LEVEL_ONE_STAT_VALUE) * (currentLevel / 100f) + 
				      (currentLevel / 100f) * attunementValues.magicDef +
				      (currentLevel / 100f) * experienceValues.magicDef)
					  * buffs.getMagicDefMultiplier());
	}
	
	public int getBuffedPhysAtk() {
		return (int) ((LEVEL_ONE_STAT_VALUE + 
				      (base.basePhysAtk - LEVEL_ONE_STAT_VALUE) * (currentLevel / 100f) + 
				      (currentLevel / 100f) * attunementValues.physAtk +
				      (currentLevel / 100f) * experienceValues.physAtk)
					  * buffs.getPhysAtkMultiplier());
	}
	
	public int getBuffedPhysDef() {
		return (int) ((LEVEL_ONE_STAT_VALUE + 
				      (base.basePhysDef - LEVEL_ONE_STAT_VALUE) * (currentLevel / 100f) + 
				      (currentLevel / 100f) * attunementValues.physDef +
				      (currentLevel / 100f) * experienceValues.physDef)
					  * buffs.getPhysDefMultiplier());
	}
	

	public int getBuffedPen() {
		return (int) ((LEVEL_ONE_STAT_VALUE + 
			      (base.basePenetration - LEVEL_ONE_STAT_VALUE) * (currentLevel / 100f) + 
			      (currentLevel / 100f) * attunementValues.penetration +
			      (currentLevel / 100f) * experienceValues.penetration)
				  * buffs.getPenMultiplier());
	}
	
	public int getBuffedRes() {
		return (int) ((LEVEL_ONE_STAT_VALUE + 
				      (base.baseResistance - LEVEL_ONE_STAT_VALUE) * (currentLevel / 100f) + 
				      (currentLevel / 100f) * attunementValues.resistance +
				      (currentLevel / 100f) * experienceValues.resistance)
					  * buffs.getResMultiplier());
	}
	
	public int getBuffedSpeed() {
		return (int) ((LEVEL_ONE_STAT_VALUE + 
			      (base.baseSpeed - LEVEL_ONE_STAT_VALUE) * (currentLevel / 100f) + 
			      (currentLevel / 100f) * attunementValues.speed +
			      (currentLevel / 100f) * experienceValues.speed)
				  * buffs.getSpeedMultiplier());
	}
	
	public int getAttunementValue(StatType stat) {
		switch(stat) {
		
		case FOCUS:
			return attunementValues.focus;
			
		case MAGIC_ATK:
			return attunementValues.magicAtk;
			
		case MAGIC_DEF:
			return attunementValues.magicDef;
			
		case PENETRATION:
			return attunementValues.penetration;
			
		case PHYS_ATK:
			return attunementValues.physAtk;
					
		case PHYS_DEF:
			return attunementValues.physDef;
			
		case RESISTANCE:
			return attunementValues.resistance;
			
		case SPEED:
			return attunementValues.speed;
			
		case VITAE:
			return attunementValues.vitae;
			
		default:
			return -1;	
		
		}
	}
	
}
















