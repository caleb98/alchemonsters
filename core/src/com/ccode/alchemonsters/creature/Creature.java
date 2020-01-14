package com.ccode.alchemonsters.creature;

import java.io.Serializable;
import java.util.ArrayList;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.ailments.StatusAilment;
import com.ccode.alchemonsters.creature.equip.Affix;
import com.ccode.alchemonsters.creature.equip.Amplifier;
import com.ccode.alchemonsters.util.DynamicVariables;

public class Creature {

	// === STATIC FIELDS ===
	public static final int LEVEL_ONE_STAT_VALUE = 5;
	public static final float BASE_CRIT_CHANCE = 0.05f;
	public static final float BASE_CRIT_MULTIPLIER = 1.5f;
	public static final float BASE_FLINCH_CHANCE = 0.0f;
	public static final float BASE_DODGE_CHANCE = 0.0f;
	public static final float BASE_STAB_MULTIPLIER = 1.5f;
	
	public static final float POSITIVE_NATURE_MULTIPLIER = 1.1f;
	public static final float NEGATIVE_NATURE_MULTIPLIER = 0.9f;
	
	public static final float PYRONIMBUS_MAGIC_ATK_MULTIPLIER = 1.2f;
	public static final float PYRONIMBUS_PHYS_ATK_MULTIPLIER = 1.2f;
	public static final float SANDSTORM_MAGIC_DEF_MULTIPLIER = 1.2f;
	public static final float LOCUST_SWARM_RES_MULTIPLIER = 1.2f;
	public static final float JETSTREAM_SPEED_MULTIPLIER = 1.2f;
	
	public static final IntStatCalculator defaultTotalVitaeCalculator = (IntStatCalculator & Serializable)(c)->{
		int pre = Math.round(
				(LEVEL_ONE_STAT_VALUE + ((c.base.baseVitae - LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
						(c.vitaeAttunement * c.currentLevel / 100f)  + 
						(c.vitaeLearned * c.currentLevel / 100f)) 
				* c.vitaeIncreases * c.vitaeEffectiveness);
		
		if(c.nature.increased == StatType.VITAE) {
			pre *= POSITIVE_NATURE_MULTIPLIER;
		}
		else if(c.nature.decreased == StatType.VITAE) {
			pre *= NEGATIVE_NATURE_MULTIPLIER;
		}
		return pre;
	};
	
	public static final IntStatCalculator defaultTotalFocusCalculator = (IntStatCalculator & Serializable)(c)->{
		int pre = Math.round(
				(LEVEL_ONE_STAT_VALUE + ((c.base.baseFocus - LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
						(c.focusAttunement * c.currentLevel / 100f) + 
						(c.focusLearned * c.currentLevel / 100f)) 
				* c.focusIncreases * c.focusEffectiveness);
		
		if(c.nature.increased == StatType.FOCUS) {
			pre *= POSITIVE_NATURE_MULTIPLIER;
		}
		else if(c.nature.decreased == StatType.FOCUS) {
			pre *= NEGATIVE_NATURE_MULTIPLIER;
		}
		return pre;
	};
	
	public static final IntStatCalculator defaultTotalMagicAtkCalculator = (IntStatCalculator & Serializable)(c)->{
		int pre = Math.round(
				(LEVEL_ONE_STAT_VALUE + ((c.base.baseMagicAtk - LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
						c.magicAtkAttunement + 
						c.magicAtkLearned) 
				* c.magicAtkIncreases * c.magicAtkEffectiveness * c.buffs.getMagicAtkMultiplier());
		
		if(c.nature.increased == StatType.MAGIC_ATK) {
			pre *= POSITIVE_NATURE_MULTIPLIER;
		}
		else if(c.nature.decreased == StatType.MAGIC_ATK) {
			pre *= NEGATIVE_NATURE_MULTIPLIER;
		}
		return pre;
	};
	
	public static final IntStatCalculator defaultTotalMagicDefCalculator = (IntStatCalculator & Serializable)(c)->{
		int pre =  Math.round(
				(LEVEL_ONE_STAT_VALUE + ((c.base.baseMagicDef - LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
						(c.magicDefAttunement * c.currentLevel / 100f) + 
						(c.magicDefLearned * c.currentLevel / 100f)) 
				* c.magicDefIncreases * c.magicDefEffectiveness * c.buffs.getMagicDefMultiplier());
		
		if(c.nature.increased == StatType.MAGIC_DEF) {
			pre *= POSITIVE_NATURE_MULTIPLIER;
		}
		else if(c.nature.decreased == StatType.MAGIC_DEF) {
			pre *= NEGATIVE_NATURE_MULTIPLIER;
		}
		return pre;
	};
	
	public static final IntStatCalculator defaultTotalPhysAtkCalculator = (IntStatCalculator & Serializable)(c)->{
		int pre =  Math.round(
				(LEVEL_ONE_STAT_VALUE + ((c.base.basePhysAtk - LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
						(c.physAtkAttunement * c.currentLevel / 100f) + 
						(c.physAtkLearned * c.currentLevel / 100f)) 
				* c.physAtkIncreases * c.physAtkEffectiveness * c.buffs.getPhysAtkMultiplier());
		
		if(c.nature.increased == StatType.PHYS_ATK) {
			pre *= POSITIVE_NATURE_MULTIPLIER;
		}
		else if(c.nature.decreased == StatType.PHYS_ATK) {
			pre *= NEGATIVE_NATURE_MULTIPLIER;
		}
		return pre;
	};
	
	public static final IntStatCalculator defaultTotalPhysDefCalculator = (IntStatCalculator & Serializable)(c)->{
		int pre = Math.round(
				(LEVEL_ONE_STAT_VALUE + ((c.base.basePhysDef - LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
						(c.physDefAttunement * c.currentLevel / 100f) + 
						(c.physDefLearned * c.currentLevel / 100f)) 
				* c.physDefIncreases * c.physDefEffectiveness * c.buffs.getPhysDefMultiplier());
		 
		if(c.nature.increased == StatType.PHYS_DEF) {
			pre *= POSITIVE_NATURE_MULTIPLIER;
		}
		else if(c.nature.decreased == StatType.PHYS_DEF) {
			pre *= NEGATIVE_NATURE_MULTIPLIER;
		}
		return pre;
	};
	
	public static final IntStatCalculator defaultTotalSpeedCalculator = (IntStatCalculator & Serializable)(c)->{
		int pre = Math.round(
				(LEVEL_ONE_STAT_VALUE + ((c.base.baseSpeed - LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
						(c.speedAttunement * c.currentLevel / 100f) + 
						(c.speedLearned * c.currentLevel / 100f)) 
				* c.speedIncreases * c.speedEffectiveness * c.buffs.getSpeedMultiplier());
		
		if(c.nature.increased == StatType.SPEED) {
			pre *= POSITIVE_NATURE_MULTIPLIER;
		}
		else if(c.nature.decreased == StatType.SPEED) {
			pre *= NEGATIVE_NATURE_MULTIPLIER;
		}
		return pre;
	};
	
	public static final IntStatCalculator defaultTotalPenCalculator = (IntStatCalculator & Serializable)(c)->{
		int pre = Math.round(
				(LEVEL_ONE_STAT_VALUE + ((c.base.basePenetration - LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
						(c.penAttunement  * c.currentLevel / 100f) + 
						(c.penLearned * c.currentLevel / 100f)) 
				* c.penIncreases * c.penEffectiveness * c.buffs.getPenMultiplier());
		
		if(c.nature.increased == StatType.PENETRATION) {
			pre *= POSITIVE_NATURE_MULTIPLIER;
		}
		else if(c.nature.decreased == StatType.PENETRATION) {
			pre *= NEGATIVE_NATURE_MULTIPLIER;
		}
		return pre;
	};
	
	public static final IntStatCalculator defaultTotalResCalculator = (IntStatCalculator & Serializable)(c)->{
		int pre = Math.round(
				(LEVEL_ONE_STAT_VALUE + ((c.base.baseResistance - LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
						(c.resAttunement * c.currentLevel / 100f) + 
						(c.resLearned * c.currentLevel / 100f)) 
				* c.resIncreases * c.resEffectiveness * c.buffs.getResMultiplier());
		
		if(c.nature.increased == StatType.RESISTANCE) {
			pre *= POSITIVE_NATURE_MULTIPLIER;
		}
		else if(c.nature.decreased == StatType.RESISTANCE) {
			pre *= NEGATIVE_NATURE_MULTIPLIER;
		}
		return pre;
	};
	
	public static final FloatStatCalculator defaultCritChanceCalculator = 
			(FloatStatCalculator & Serializable)(c)->{ return BASE_CRIT_CHANCE + c.critChanceIncrease; };
			
	public static final FloatStatCalculator defaultCritMultiplierCalculator = 
			(FloatStatCalculator & Serializable)(c)->{ return BASE_CRIT_MULTIPLIER + c.critMultiplierIncrease; };
			
	public static final FloatStatCalculator defaultFlinchChanceCalculator = 
			(FloatStatCalculator & Serializable)(c)->{ return BASE_FLINCH_CHANCE + c.flinchChanceIncrease; };
			
	public static final FloatStatCalculator defaultDodgeChanceCalculator = 
			(FloatStatCalculator & Serializable)(c)->{ return BASE_DODGE_CHANCE + c.dodgeChanceIncrease; };
			
	public static final FloatStatCalculator defaultStabMultiplierCalculator = 
			(FloatStatCalculator & Serializable)(c)->{ return BASE_STAB_MULTIPLIER + c.stabMultiplierIncrease; };
	
	public static final IntStatCalculator defaultHealthCalculator = (IntStatCalculator & Serializable)(c)->{
		return Math.round((c.baseHealth + 
				(c.currentLevel * (c.base.maxBaseHealth - c.base.minBaseHealth) / 10f) +
				(c.currentLevel * c.totalVitae / 50f) +
				c.healthIncreases) * c.healthMultiplier);
	};
	
	public static final IntStatCalculator defaultManaCalculator = (IntStatCalculator & Serializable)(c)->{
		return Math.round((c.baseMana + 
				(c.currentLevel * (c.base.maxBaseMana - c.base.minBaseMana) / 10f) +
				(c.currentLevel * c.totalFocus / 50f) +
				c.manaIncreases) * c.manaMultiplier);
	};
	// === END STATIC FIELDS ===
	
	
	public String personalName;
	
	// === STATS AND CALCULATIONS ===	
	public CreatureNature nature;
	
	//base
	public CreatureBase base;
	
	//attunement
	public int vitaeAttunement = 0;
	public int focusAttunement = 0;
	public int magicAtkAttunement = 0;
	public int magicDefAttunement = 0;
	public int physAtkAttunement = 0;
	public int physDefAttunement = 0;
	public int penAttunement = 0;
	public int resAttunement = 0;
	public int speedAttunement = 0;
	
	//learned bonuses
	public int vitaeLearned = 0;
	public int focusLearned = 0;
	public int magicAtkLearned = 0;
	public int magicDefLearned = 0;
	public int physAtkLearned = 0;
	public int physDefLearned = 0;
	public int penLearned = 0;
	public int resLearned = 0;
	public int speedLearned = 0;
	
	//increases used for calculating total stats
	public float vitaeIncreases = 1f;
	public float focusIncreases = 1f;
	public float magicAtkIncreases = 1f;
	public float magicDefIncreases = 1f;
	public float physAtkIncreases = 1f;
	public float physDefIncreases = 1f;
	public float penIncreases = 1f;
	public float resIncreases = 1f;
	public float speedIncreases = 1f;
	
	public float vitaeEffectiveness = 1f;
	public float focusEffectiveness = 1f;
	public float magicAtkEffectiveness = 1f;
	public float magicDefEffectiveness = 1f;
	public float physAtkEffectiveness = 1f;
	public float physDefEffectiveness = 1f;
	public float penEffectiveness = 1f;
	public float resEffectiveness = 1f;
	public float speedEffectiveness = 1f;
	
	public IntStatCalculator totalVitaeCalculator = defaultTotalVitaeCalculator;
	public IntStatCalculator totalFocusCalculator = defaultTotalFocusCalculator;
	public IntStatCalculator totalMagicAtkCalculator = defaultTotalMagicAtkCalculator;
	public IntStatCalculator totalMagicDefCalculator = defaultTotalMagicDefCalculator;
	public IntStatCalculator totalPhysAtkCalculator = defaultTotalPhysAtkCalculator;
	public IntStatCalculator totalPhysDefCalculator = defaultTotalPhysDefCalculator;
	public IntStatCalculator totalSpeedCalculator = defaultTotalSpeedCalculator;
	public IntStatCalculator totalPenCalculator = defaultTotalPenCalculator;
	public IntStatCalculator totalResCalculator = defaultTotalResCalculator;
	
	//totaled stats
	public int totalVitae;
	public int totalFocus;
	public int totalMagicAtk;
	public int totalMagicDef;
	public int totalPhysAtk;
	public int totalPhysDef;
	public int totalPenetration;
	public int totalResistance;
	public int totalSpeed;
	
	//secondaries
	public float critChance = BASE_CRIT_CHANCE;
	public float critMultiplier = BASE_CRIT_MULTIPLIER;
	public float flinchChance = BASE_FLINCH_CHANCE;
	public float dodgeChance = BASE_DODGE_CHANCE;
	public float stabMultiplier = BASE_STAB_MULTIPLIER;
	
	public float critChanceIncrease = 0f;
	public float critMultiplierIncrease = 0f;
	public float flinchChanceIncrease = 0f;
	public float dodgeChanceIncrease = 0f;
	public float stabMultiplierIncrease = 0f;
	
	public FloatStatCalculator critChanceCalculator = defaultCritChanceCalculator;
	public FloatStatCalculator critMultiplieCalculator = defaultCritMultiplierCalculator;
	public FloatStatCalculator flinchChanceCalculator = defaultFlinchChanceCalculator;
	public FloatStatCalculator dodgeChanceCalculator = defaultDodgeChanceCalculator;
	public FloatStatCalculator stabMultiplierCalculator = defaultStabMultiplierCalculator;

	//derived stats
	public int baseHealth;
	public int baseMana;
	
	public int healthIncreases = 0;
	public int manaIncreases = 0;
	
	public float healthMultiplier = 1f;
	public float manaMultiplier = 1f;
	
	public int maxHealth;
	public int maxMana;
	
	//Active values
	public int currentHealth;
	public int currentMana;
	
	//effects
	public ArrayList<Effect> activeEffects = new ArrayList<>();

	public CombatMods buffs = new CombatMods();
	public StatusAilment currentAilment;
	
	public DynamicVariables variables = new DynamicVariables(); 
	
	//TODO: Equipment
	public Amplifier amplifier;	
//	public HeldItem heldItem;
	
	//Moves/Abilities
	public String[] moves = new String[]{};
	
	//Level variables
	public int currentXP;
	public int nextLevelXP;
	public int currentLevel;
	
	public boolean isDead() {
		return currentHealth <= 0;
	}
	
	/**
	 * Resets the mon's HP and Mana values
	 */
	public void resetHealthAndMana() {
		currentHealth = maxHealth;
		currentMana = maxMana;
	}
	
	public void modifyHealth(int amt) {
		currentHealth += amt;
		if(currentHealth < 0) currentHealth = 0;
		if(currentHealth > maxHealth) currentHealth = maxHealth;
	}
	
	//===========================================
	//         STAT RETRIEVAL METHODS
	//===========================================
	public int calcTotalMagicAtk(BattleContext context) {
		if(context == null) return totalMagicAtk;
		
		//Check for magic atk boost to fire types from pyronimbus
		int total = totalMagicAtk;
		if(context.battleground.weather == WeatherType.PYRONIMBUS) {
			for(ElementType t : base.types) {
				if(t == ElementType.FIRE) {
					total *= PYRONIMBUS_MAGIC_ATK_MULTIPLIER;
					break;
				}
			}
		}
		
		return total;
	}
	
	public int calcTotalMagicDef(BattleContext context) {
		if(context == null) return totalMagicDef;
		
		//Check for magic def boost to ground types from sandstorm
		int total = totalMagicDef;
		if(context.battleground.weather == WeatherType.SANDSTORM) {
			for(ElementType t : base.types) {
				if(t == ElementType.GROUND) {
					total *= SANDSTORM_MAGIC_DEF_MULTIPLIER;
					break;
				}
			}
		}
		return total;
	}
	
	public int calcTotalPhysAtk(BattleContext context) {
		if(context == null) return totalPhysAtk;
		
		int total = totalPhysAtk;
		if(context.battleground.weather == WeatherType.PYRONIMBUS) {
			for(ElementType t : base.types) {
				if(t == ElementType.FIRE) {
					total *= SANDSTORM_MAGIC_DEF_MULTIPLIER;
					break;
				}
			}
		}
		return total;
	}
	
	public int calcTotalPhysDef(BattleContext context) {
		return totalPhysDef;
	}
	
	public int calcTotalPen(BattleContext context) {
		return totalPenetration;
	}
	
	public int calcTotalRes(BattleContext context) {
		if(context == null) return totalResistance;
		
		int total = totalResistance;
		if(context.battleground.weather == WeatherType.LOCUST_SWARM) {
			for(ElementType t : base.types) {
				if(t == ElementType.UNDEAD) {
					total *= LOCUST_SWARM_RES_MULTIPLIER;
					break;
				}
			}
		}
		return total;
	}
	
	public int calcTotalSpeed(BattleContext context) {
		if(context == null) return totalSpeed;
		
		int total = totalSpeed;
		if(context.battleground.weather == WeatherType.JETSTREAM) {
			for(ElementType t : base.types) {
				if(t == ElementType.AIR) {
					total *= JETSTREAM_SPEED_MULTIPLIER;
					break;
				}
			}
		}
		return total;
	}
	
	//===========================================
	//         STAT CALCULATION METHODS
	//===========================================
	public void recalculateAllStats(boolean resetCurrentHealthAndMana) {
		
		//Reset modifiers first
		resetStatModifications();
		
		//Reset effects list
		activeEffects.clear();
		
		//Pull all changes from affixes (increase/multipliers and permanent effects)
		if(amplifier != null) {
			for(Affix a : amplifier.prefixes) {
				a.applyTo(this);
			}
			for(Affix a : amplifier.suffixes) {
				a.applyTo(this);
			}
		}
		
		//Recalculate with new values
		calculateTotalStats(resetCurrentHealthAndMana);
	}
	
	private void calculateTotalStats(boolean resetCurrentHealthAndMana) {
		totalVitae = totalVitaeCalculator.calculateStat(this);
		totalFocus = totalFocusCalculator.calculateStat(this);
		totalMagicAtk = totalMagicAtkCalculator.calculateStat(this);
		totalMagicDef = totalMagicDefCalculator.calculateStat(this);
		totalPhysAtk = totalPhysAtkCalculator.calculateStat(this);
		totalPhysDef = totalPhysDefCalculator.calculateStat(this);
		totalSpeed = totalSpeedCalculator.calculateStat(this);
		totalPenetration = totalPenCalculator.calculateStat(this);
		totalResistance = totalResCalculator.calculateStat(this);
		
		critChance = defaultCritChanceCalculator.calculateStat(this);
		critMultiplier = defaultCritMultiplierCalculator.calculateStat(this);
		flinchChance = defaultFlinchChanceCalculator.calculateStat(this);
		dodgeChance = defaultDodgeChanceCalculator.calculateStat(this);
		stabMultiplier = defaultStabMultiplierCalculator.calculateStat(this);
		
		if(resetCurrentHealthAndMana) {
			maxHealth = defaultHealthCalculator.calculateStat(this);
			maxMana = defaultManaCalculator.calculateStat(this);
			
			currentHealth = maxHealth;
			currentMana = maxMana;
		}
		else {
			float currentHealthPercent = currentHealth / (float) maxHealth;
			float currentManaPercent = currentMana / (float) maxMana;
			
			maxHealth = defaultHealthCalculator.calculateStat(this);
			maxMana = defaultManaCalculator.calculateStat(this);
			
			currentHealth = Math.round(maxHealth * currentHealthPercent);
			currentMana = Math.round(maxMana * currentManaPercent);
		}
	}
	
	private void resetStatModifications() {
		vitaeIncreases = 1f;
		focusIncreases = 1f;
		magicAtkIncreases = 1f;
		magicDefIncreases = 1f;
		physAtkIncreases = 1f;
		physDefIncreases = 1f;
		penIncreases = 1f;
		resIncreases = 1f;
		speedIncreases = 1f;
		
		vitaeEffectiveness = 1f;
		focusEffectiveness = 1f;
		magicAtkEffectiveness = 1f;
		magicDefEffectiveness = 1f;
		physAtkEffectiveness = 1f;
		physDefEffectiveness = 1f;
		penEffectiveness = 1f;
		resEffectiveness = 1f;
		speedEffectiveness = 1f;
		
		critChance = BASE_CRIT_CHANCE;        
		critMultiplier = BASE_CRIT_MULTIPLIER;
		flinchChance = BASE_FLINCH_CHANCE;    
		dodgeChance = BASE_DODGE_CHANCE;      
		stabMultiplier = BASE_STAB_MULTIPLIER;
	}
	
}
















