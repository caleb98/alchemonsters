package com.ccode.alchemonsters.creature;

import java.util.ArrayList;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.ailments.StatusAilment;
import com.ccode.alchemonsters.creature.equip.Affix;
import com.ccode.alchemonsters.creature.equip.Amplifier;
import com.ccode.alchemonsters.engine.database.StatCalculators;
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
	
	public String totalVitaeCalculator = StatCalculators.defaultTotalVitaeCalculator;
	public String totalFocusCalculator = StatCalculators.defaultTotalFocusCalculator;
	public String totalMagicAtkCalculator = StatCalculators.defaultTotalMagicAtkCalculator;
	public String totalMagicDefCalculator = StatCalculators.defaultTotalMagicDefCalculator;
	public String totalPhysAtkCalculator = StatCalculators.defaultTotalPhysAtkCalculator;
	public String totalPhysDefCalculator = StatCalculators.defaultTotalPhysDefCalculator;
	public String totalSpeedCalculator = StatCalculators.defaultTotalSpeedCalculator;
	public String totalPenCalculator = StatCalculators.defaultTotalPenCalculator;
	public String totalResCalculator = StatCalculators.defaultTotalResCalculator;
	
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
	
	public String critChanceCalculator = StatCalculators.defaultCritChanceCalculator;
	public String critMultiplieCalculator = StatCalculators.defaultCritMultiplierCalculator;
	public String flinchChanceCalculator = StatCalculators.defaultFlinchChanceCalculator;
	public String dodgeChanceCalculator = StatCalculators.defaultDodgeChanceCalculator;
	public String stabMultiplierCalculator = StatCalculators.defaultStabMultiplierCalculator;

	//derived stats
	public int baseHealth;
	public int baseMana;
	
	public int healthIncreases = 0;
	public int manaIncreases = 0;
	
	public float healthMultiplier = 1f;
	public float manaMultiplier = 1f;
	
	public int maxHealth;
	public int maxMana;
	
	public String defaultHealthCalculator = StatCalculators.defaultHealthCalculator;
	public String defaultManaCalculator = StatCalculators.defaultManaCalculator;
	
	//Active values
	public int currentHealth;
	public int currentMana;
	
	//effects
	public ArrayList<Effect> activeEffects = new ArrayList<>();

	public CombatMods mods = new CombatMods();
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
		totalVitae = StatCalculators.doIntCalc(totalVitaeCalculator, this);
		totalFocus = StatCalculators.doIntCalc(totalFocusCalculator, this);
		totalMagicAtk = StatCalculators.doIntCalc(totalMagicAtkCalculator, this);
		totalMagicDef = StatCalculators.doIntCalc(totalMagicDefCalculator, this);
		totalPhysAtk = StatCalculators.doIntCalc(totalPhysAtkCalculator, this);
		totalPhysDef = StatCalculators.doIntCalc(totalPhysDefCalculator, this);
		totalSpeed = StatCalculators.doIntCalc(totalSpeedCalculator, this);
		totalPenetration = StatCalculators.doIntCalc(totalPenCalculator, this);
		totalResistance = StatCalculators.doIntCalc(totalResCalculator, this);
		
		critChance = StatCalculators.doFloatCalc(critChanceCalculator, this);
		critMultiplier = StatCalculators.doFloatCalc(critMultiplieCalculator, this);
		flinchChance = StatCalculators.doFloatCalc(flinchChanceCalculator, this);
		dodgeChance = StatCalculators.doFloatCalc(dodgeChanceCalculator, this);
		stabMultiplier = StatCalculators.doFloatCalc(stabMultiplierCalculator, this);
		
		if(resetCurrentHealthAndMana) {
			maxHealth = StatCalculators.doIntCalc(defaultHealthCalculator, this);
			maxMana = StatCalculators.doIntCalc(defaultManaCalculator, this);
			
			currentHealth = maxHealth;
			currentMana = maxMana;
		}
		else {
			float currentHealthPercent = currentHealth / (float) maxHealth;
			float currentManaPercent = currentMana / (float) maxMana;
			
			maxHealth = StatCalculators.doIntCalc(defaultHealthCalculator, this);
			maxMana = StatCalculators.doIntCalc(defaultManaCalculator, this);
			
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
















