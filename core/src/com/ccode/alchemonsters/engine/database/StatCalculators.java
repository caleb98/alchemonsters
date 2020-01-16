package com.ccode.alchemonsters.engine.database;

import java.io.Serializable;
import java.util.HashMap;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.StatCalculator;
import com.ccode.alchemonsters.creature.StatType;

public class StatCalculators {

	private static HashMap<String, StatCalculator> CALCULATOR_DICTIONARY;
	private static boolean isInitialized = false;
	
	public static final String defaultTotalVitaeCalculator = "DefaultTotalVitaeCalculator";
	public static final String defaultTotalFocusCalculator = "DefaultTotalFocusCalculator";
	public static final String defaultTotalMagicAtkCalculator = "DefaultTotalMagicAtkCalculator";
	public static final String defaultTotalMagicDefCalculator = "DefaultTotalMagicDefCalculator";
	public static final String defaultTotalPhysAtkCalculator = "DefaultTotalPhysAtkCalculator";
	public static final String defaultTotalPhysDefCalculator = "DefaultTotalPhysDefCalculator";
	public static final String defaultTotalSpeedCalculator = "DefaultTotalSpeedCalculator";
	public static final String defaultTotalPenCalculator = "DefaultTotalPenCalculator";
	public static final String defaultTotalResCalculator = "DefaultTotalResCalculator";
	
	public static final String defaultCritChanceCalculator = "DefaultCritChanceCalculator";
	public static final String defaultCritMultiplierCalculator = "DefaultCritMultiplierCalculator";
	public static final String defaultFlinchChanceCalculator = "DefaultFlinchChanceCalculator";
	public static final String defaultDodgeChanceCalculator = "DefaultDodgeChanceCalculator";
	public static final String defaultStabMultiplierCalculator = "DefaultStabMultiplierCalculator";
	
	public static final String defaultHealthCalculator = "DefaultHealthCalculator";
	public static final String defaultManaCalculator = "DefaultManaCalculator";
	
	public static void init() {
		CALCULATOR_DICTIONARY = new HashMap<>();
		
		//Add default calculators
		CALCULATOR_DICTIONARY.put(defaultTotalVitaeCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			int pre = Math.round(
					(Creature.LEVEL_ONE_STAT_VALUE + ((c.base.baseVitae - Creature.LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
							(c.vitaeAttunement * c.currentLevel / 100f)  + 
							(c.vitaeLearned * c.currentLevel / 100f)) 
					* c.vitaeIncreases * c.vitaeEffectiveness);
			
			if(c.nature.increased == StatType.VITAE) {
				pre *= Creature.POSITIVE_NATURE_MULTIPLIER;
			}
			else if(c.nature.decreased == StatType.VITAE) {
				pre *= Creature.NEGATIVE_NATURE_MULTIPLIER;
			}
			return pre;
		});
		
		CALCULATOR_DICTIONARY.put(defaultTotalFocusCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			int pre = Math.round(
					(Creature.LEVEL_ONE_STAT_VALUE + ((c.base.baseFocus - Creature.LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
							(c.focusAttunement * c.currentLevel / 100f) + 
							(c.focusLearned * c.currentLevel / 100f)) 
					* c.focusIncreases * c.focusEffectiveness);
			
			if(c.nature.increased == StatType.FOCUS) {
				pre *= Creature.POSITIVE_NATURE_MULTIPLIER;
			}
			else if(c.nature.decreased == StatType.FOCUS) {
				pre *= Creature.NEGATIVE_NATURE_MULTIPLIER;
			}
			return pre;
		});
		
		
		CALCULATOR_DICTIONARY.put(defaultTotalMagicAtkCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			int pre = Math.round(
					(Creature.LEVEL_ONE_STAT_VALUE + ((c.base.baseMagicAtk - Creature.LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
							c.magicAtkAttunement + 
							c.magicAtkLearned) 
					* c.magicAtkIncreases * c.magicAtkEffectiveness * c.buffs.getMagicAtkMultiplier());
			
			if(c.nature.increased == StatType.MAGIC_ATK) {
				pre *= Creature.POSITIVE_NATURE_MULTIPLIER;
			}
			else if(c.nature.decreased == StatType.MAGIC_ATK) {
				pre *= Creature.NEGATIVE_NATURE_MULTIPLIER;
			}
			return pre;
		});
		
		CALCULATOR_DICTIONARY.put(defaultTotalMagicDefCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			int pre =  Math.round(
					(Creature.LEVEL_ONE_STAT_VALUE + ((c.base.baseMagicDef - Creature.LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
							(c.magicDefAttunement * c.currentLevel / 100f) + 
							(c.magicDefLearned * c.currentLevel / 100f)) 
					* c.magicDefIncreases * c.magicDefEffectiveness * c.buffs.getMagicDefMultiplier());
			
			if(c.nature.increased == StatType.MAGIC_DEF) {
				pre *= Creature.POSITIVE_NATURE_MULTIPLIER;
			}
			else if(c.nature.decreased == StatType.MAGIC_DEF) {
				pre *= Creature.NEGATIVE_NATURE_MULTIPLIER;
			}
			return pre;
		});
		
		CALCULATOR_DICTIONARY.put(defaultTotalPhysAtkCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			int pre =  Math.round(
					(Creature.LEVEL_ONE_STAT_VALUE + ((c.base.basePhysAtk - Creature.LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
							(c.physAtkAttunement * c.currentLevel / 100f) + 
							(c.physAtkLearned * c.currentLevel / 100f)) 
					* c.physAtkIncreases * c.physAtkEffectiveness * c.buffs.getPhysAtkMultiplier());
			
			if(c.nature.increased == StatType.PHYS_ATK) {
				pre *= Creature.POSITIVE_NATURE_MULTIPLIER;
			}
			else if(c.nature.decreased == StatType.PHYS_ATK) {
				pre *= Creature.NEGATIVE_NATURE_MULTIPLIER;
			}
			return pre;
		});
		
		CALCULATOR_DICTIONARY.put(defaultTotalPhysDefCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			int pre = Math.round(
					(Creature.LEVEL_ONE_STAT_VALUE + ((c.base.basePhysDef - Creature.LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
							(c.physDefAttunement * c.currentLevel / 100f) + 
							(c.physDefLearned * c.currentLevel / 100f)) 
					* c.physDefIncreases * c.physDefEffectiveness * c.buffs.getPhysDefMultiplier());
			 
			if(c.nature.increased == StatType.PHYS_DEF) {
				pre *= Creature.POSITIVE_NATURE_MULTIPLIER;
			}
			else if(c.nature.decreased == StatType.PHYS_DEF) {
				pre *= Creature.NEGATIVE_NATURE_MULTIPLIER;
			}
			return pre;
		});
		
		CALCULATOR_DICTIONARY.put(defaultTotalSpeedCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			int pre = Math.round(
					(Creature.LEVEL_ONE_STAT_VALUE + ((c.base.baseSpeed - Creature.LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
							(c.speedAttunement * c.currentLevel / 100f) + 
							(c.speedLearned * c.currentLevel / 100f)) 
					* c.speedIncreases * c.speedEffectiveness * c.buffs.getSpeedMultiplier());
			
			if(c.nature.increased == StatType.SPEED) {
				pre *= Creature.POSITIVE_NATURE_MULTIPLIER;
			}
			else if(c.nature.decreased == StatType.SPEED) {
				pre *= Creature.NEGATIVE_NATURE_MULTIPLIER;
			}
			return pre;
		});
		
		CALCULATOR_DICTIONARY.put(defaultTotalPenCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			int pre = Math.round(
					(Creature.LEVEL_ONE_STAT_VALUE + ((c.base.basePenetration - Creature.LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
							(c.penAttunement  * c.currentLevel / 100f) + 
							(c.penLearned * c.currentLevel / 100f)) 
					* c.penIncreases * c.penEffectiveness * c.buffs.getPenMultiplier());
			
			if(c.nature.increased == StatType.PENETRATION) {
				pre *= Creature.POSITIVE_NATURE_MULTIPLIER;
			}
			else if(c.nature.decreased == StatType.PENETRATION) {
				pre *= Creature.NEGATIVE_NATURE_MULTIPLIER;
			}
			return pre;
		});
		
		CALCULATOR_DICTIONARY.put(defaultTotalResCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			int pre = Math.round(
					(Creature.LEVEL_ONE_STAT_VALUE + ((c.base.baseResistance - Creature.LEVEL_ONE_STAT_VALUE) * (c.currentLevel / 100f)) + 
							(c.resAttunement * c.currentLevel / 100f) + 
							(c.resLearned * c.currentLevel / 100f)) 
					* c.resIncreases * c.resEffectiveness * c.buffs.getResMultiplier());
			
			if(c.nature.increased == StatType.RESISTANCE) {
				pre *= Creature.POSITIVE_NATURE_MULTIPLIER;
			}
			else if(c.nature.decreased == StatType.RESISTANCE) {
				pre *= Creature.NEGATIVE_NATURE_MULTIPLIER;
			}
			return pre;
		});
		
		CALCULATOR_DICTIONARY.put(defaultCritChanceCalculator, 
				(StatCalculator<Float> & Serializable)(c)->{ return Creature.BASE_CRIT_CHANCE + c.critChanceIncrease; });
		
		CALCULATOR_DICTIONARY.put(defaultCritMultiplierCalculator, 
				(StatCalculator<Float> & Serializable)(c)->{ return Creature.BASE_CRIT_MULTIPLIER + c.critMultiplierIncrease; });
		
		CALCULATOR_DICTIONARY.put(defaultFlinchChanceCalculator, 
				(StatCalculator<Float> & Serializable)(c)->{ return Creature.BASE_FLINCH_CHANCE + c.flinchChanceIncrease; });
		
		CALCULATOR_DICTIONARY.put(defaultDodgeChanceCalculator, 
				(StatCalculator<Float> & Serializable)(c)->{ return Creature.BASE_DODGE_CHANCE + c.dodgeChanceIncrease; });
		
		CALCULATOR_DICTIONARY.put(defaultStabMultiplierCalculator, 
				(StatCalculator<Float> & Serializable)(c)->{ return Creature.BASE_STAB_MULTIPLIER + c.stabMultiplierIncrease; });
		
		CALCULATOR_DICTIONARY.put(defaultHealthCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			return Math.round((c.baseHealth + 
					(c.currentLevel * (c.base.maxBaseHealth - c.base.minBaseHealth) / 10f) +
					(c.currentLevel * c.totalVitae / 50f) +
					c.healthIncreases) * c.healthMultiplier);
		});
		
		CALCULATOR_DICTIONARY.put(defaultManaCalculator, (StatCalculator<Integer> & Serializable)(c)->{
			return Math.round((c.baseMana + 
					(c.currentLevel * (c.base.maxBaseMana - c.base.minBaseMana) / 10f) +
					(c.currentLevel * c.totalFocus / 50f) +
					c.manaIncreases) * c.manaMultiplier);
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> StatCalculator<T> getCalculator(String name, Class<T> type) {
		if(!isInitialized) {
			throw new IllegalStateException("Attempted to retreive stat calculator before initializing.");
		}
		
		 return CALCULATOR_DICTIONARY.get(name);
	}
	
	public static StatCalculator<Integer> getIntCalculator(String name) {
		return getCalculator(name, Integer.class);
	}
	
	public static StatCalculator<Float> getFloatCalculator(String name) {
		return getCalculator(name, Float.class);
	}
	
	public static int doIntCalc(String calcName, Creature c) {
		return getCalculator(calcName, Integer.class).calculateStat(c);
	}
	
	public static float doFloatCalc(String calcName, Creature c) {
		return getCalculator(calcName, Integer.class).calculateStat(c);
	}
	
}




























