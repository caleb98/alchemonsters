package com.ccode.alchemonsters.creature;

import java.util.ArrayList;

import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.combat.effect.Ailment;
import com.ccode.alchemonsters.combat.effect.Effect;
import com.ccode.alchemonsters.creature.equip.Affix;
import com.ccode.alchemonsters.creature.equip.Amplifier;
import com.ccode.alchemonsters.util.DynamicVariables;

public class Creature {

	// === STATIC FIELDS ===
	public static final int LEVEL_ONE_STAT_VALUE = 5;
	public static final float BASE_CRIT_CHANCE = 0.05f;
	public static final float BASE_CRIT_MULTIPLIER = 1.5f;
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
	private int vitaeIncreases = 1;
	private int focusIncreases = 1;
	private int magicAtkIncreases = 1;
	private int magicDefIncreases = 1;
	private int physAtkIncreases = 1;
	private int physDefIncreases = 1;
	private int penIncreases = 1;
	private int resIncreases = 1;
	private int speedIncreases = 1;
	
	private float vitaeEffectiveness = 1f;
	private float focusEffectiveness = 1f;
	private float magicAtkEffectiveness = 1f;
	private float magicDefEffectiveness = 1f;
	private float physAtkEffectiveness = 1f;
	private float physDefEffectiveness = 1f;
	private float penEffectiveness = 1f;
	private float resEffectiveness = 1f;
	private float speedEffectiveness = 1f;
	
	public String totalVitaeCalculator = StatCalculators.defaultTotalVitaeCalculator;
	public String totalFocusCalculator = StatCalculators.defaultTotalFocusCalculator;
	public String totalMagicAtkCalculator = StatCalculators.defaultTotalMagicAtkCalculator;
	public String totalMagicDefCalculator = StatCalculators.defaultTotalMagicDefCalculator;
	public String totalPhysAtkCalculator = StatCalculators.defaultTotalPhysAtkCalculator;
	public String totalPhysDefCalculator = StatCalculators.defaultTotalPhysDefCalculator;
	public String totalSpeedCalculator = StatCalculators.defaultTotalSpeedCalculator;
	public String totalPenCalculator = StatCalculators.defaultTotalPenCalculator;
	public String totalResCalculator = StatCalculators.defaultTotalResCalculator;
	
	//secondaries	
	private float critChanceIncrease = 0f;
	private float critMultiplierIncrease = 0f;
	private float dodgeChanceIncrease = 0f;
	private float stabMultiplierIncrease = 0f;
	
	private float critChanceEffectiveness = 1f;
	private float critMultiplierEffectiveness = 1f;
	private float dodgeChanceEffectiveness = 1f;
	private float stabMultiplierEffectiveness = 1f;
	
	public String critChanceCalculator = StatCalculators.defaultCritChanceCalculator;
	public String critMultiplieCalculator = StatCalculators.defaultCritMultiplierCalculator;
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
	private int currentHealth;
	private int currentMana;
	
	//Effects and Ailments
	public ArrayList<Effect> activeEffects = new ArrayList<>();
	public ArrayList<Ailment> activeAilments = new ArrayList<>();
	public Ailment strongAilment;

	public CombatMods mods = new CombatMods();
	
	public DynamicVariables variables = new DynamicVariables(); 
	
	//TODO: Equipment
	public Amplifier amplifier;	
	//public HeldItem heldItem;
	
	//Moves/Abilities
	public String[] moves = new String[]{};
	
	//Level variables
	public int currentXP;
	public int nextLevelXP;
	public int currentLevel;
	
	public boolean isDead() {
		return currentHealth <= 0;
	}
	
	public boolean hasAilment(String ailmentName) {
		for(Ailment a : activeAilments) {
			if(a.name.equals(ailmentName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasEffect(String effectName) {
		for(Effect e : activeEffects) {
			if(e.name.equals(effectName)) {
				return true;
			}
		}
		return false;
	}
	
	public void addAilment(Ailment a) {
		if(a.isStrong) {
			if(strongAilment != null) {
				strongAilment.onRemove(this);
			}
			strongAilment = a;
			a.onApply(this);
		}
		else {
			activeAilments.add(a);
			a.onApply(this);
		}
	}
	
	public void removeAilment(Ailment a) {
		if(a.isStrong) {
			if(strongAilment == a) {
				a.onRemove(this);
				strongAilment = null;
			}
		}
		else {
			if(activeAilments.remove(a)) 
				a.onRemove(this);
		}
	}
	
	public void addEffect(Effect e) {
		activeEffects.add(e);
		e.onApply(this);
	}
	
	public void removeEffect(Effect e) {
		if(activeEffects.remove(e))
			e.onRemove(this);
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
		else if(currentHealth > maxHealth) currentHealth = maxHealth;
	}
	
	public int getCurrentHealth() {
		return currentHealth;
	}
	
	public void modifyMana(int amt) {
		currentMana += amt;
		if(currentMana < 0) currentMana = 0;
		else if(currentMana > maxMana) currentMana = maxMana;
	}
	
	public int getCurrentMana() {
		return currentMana;
	}
	
	//===========================================
	//         STAT MODIFIER METHODS
	//===========================================
	public int getVitaeIncreases() {
		return vitaeIncreases;
	}
	
	public void addVitaeIncrease(int value) {
		vitaeIncreases += value;
	}
	
	public void removeVitaeIncrease(int value) {
		vitaeIncreases -= value;
	}
	
	public int getFocusIncreases() {
		return focusIncreases;
	}
	
	public void addFocusIncrease(int value) {
		focusIncreases += value;
	}
	
	public void removeFocusIncrease(int value) {
		focusIncreases -= value;
	}
	
	public int getMagicAtkIncreases() {
		return magicAtkIncreases;
	}
	
	public void addMagicAtkIncrease(int value) {
		magicAtkIncreases += value;
	}
	
	public void removeMagicAtkIncrease(int value) {
		magicAtkIncreases -= value;
	}
	
	public int getMagicDefIncreases() {
		return magicDefIncreases;
	}
	
	public void addMagicDefIncrease(int value) {
		magicDefIncreases += value;
	}
	
	public void removeMagicDefIncrease(int value) {
		magicDefIncreases -= value;
	}
	
	public int getPhysAtkIncreases() {
		return physAtkIncreases;
	}
	
	public void addPhysAtkIncrease(int value) {
		physAtkIncreases += value;
	}
	
	public void removePhysAtkIncrease(int value) {
		physAtkIncreases -= value;
	}
	
	public int getPhysDefIncreases() {
		return physDefIncreases;
	}
	
	public void addPhysDefIncrease(int value) {
		physDefIncreases += value;
	}
	
	public void removePhysDefIncrease(int value) {
		physDefIncreases -= value;
	}
	
	public int getPenIncreases() {
		return penIncreases;
	}
	
	public void addPenIncrease(int value) {
		penIncreases += value;
	}
	
	public void removePenIncrease(int value) {
		penIncreases -= value;
	}
	
	public int getResIncreases() {
		return resIncreases;
	}
	
	public void addResIncrease(int value) {
		resIncreases += value;
	}
	
	public void removeResIncrease(int value) {
		resIncreases -= value;
	}
	
	public int getSpeedIncreases() {
		return speedIncreases;
	}
	
	public void addSpeedIncrease(int value) {
		speedIncreases += value;
	}
	
	public void removeSpeedIncrease(int value) {
		speedIncreases -= value;
	}
	
	public float getCritChanceIncrease() {
		return critChanceIncrease;
	}
	
	public void addCritChanceIncrease(float value) {
		critChanceIncrease += value;
	}
	
	public void removeCritChanceIncrease(float value) {
		critChanceIncrease -= value;
	}
	
	public float getCritMultiplierIncrease() {
		return critMultiplierIncrease;
	}
	
	public void addCritMultiplierIncrease(float value) {
		critMultiplierIncrease += value;
	}
	
	public void removeCritMultiplierIncrease(float value) {
		critMultiplierIncrease -= value;
	}
	
	public float getDodgeChanceIncrease() {
		return dodgeChanceIncrease;
	}
	
	public void addDodgeChanceIncrease(float value) {
		dodgeChanceIncrease += value;
	}
	
	public void removeDodgeChanceIncrease(float value) {
		dodgeChanceIncrease -= value;
	}
	
	public float getStabMultiplierIncrease() {
		return stabMultiplierIncrease;
	}
	
	public void addStabMultiplierIncrease(float value) {
		stabMultiplierIncrease += value;
	}
	
	public void removeStabMultiplierIncrease(float value) {
		stabMultiplierIncrease -= value;
	}
	
	public float getVitaeEffectiveness() {
		return vitaeEffectiveness;
	}
	
	public void increaseVitaeEffectiveness(float value) {
		vitaeEffectiveness *= value;
	}
	
	public void removeVitaeEffectivenessIncrease(float value) {
		vitaeEffectiveness /= value;
	}
	
	public float getFocusEffectiveness() {
		return focusEffectiveness;
	}
	
	public void increaseFocusEffectiveness(float value) {
		focusEffectiveness *= value;
	}
	
	public void removeFocusEffectivenessIncrease(float value) {
		focusEffectiveness /= value;
	}
	
	public float getMagicAtkEffectiveness() {
		return magicAtkEffectiveness;
	}
	
	public void increaseMagicAtkEffectiveness(float value) {
		magicAtkEffectiveness *= value;
	}
	
	public void removeMagicAtkEffectivenessIncrease(float value) {
		magicAtkEffectiveness /= value;
	}
	
	public float getMagicDefEffectiveness() {
		return magicDefEffectiveness;
	}
	
	public void increaseMagicDefEffectiveness(float value) {
		magicDefEffectiveness *= value;
	}
	
	public void removeMagicDefEffectivenessIncrease(float value) {
		magicDefEffectiveness /= value;
	}
	
	public float getPhysAtkEffectiveness() {
		return physAtkEffectiveness;
	}
	
	public void increasePhysAtkEffectiveness(float value) {
		physAtkEffectiveness *= value;
	}
	
	public void removePhysAtkEffectivenessIncrease(float value) {
		physAtkEffectiveness /= value;
	}
	
	public float getPhysDefEffectiveness() {
		return physDefEffectiveness;
	}
	
	public void increasePhysDefEffectiveness(float value) {
		physDefEffectiveness *= value;
	}
	
	public void removePhysDefEffectivenessIncrease(float value) {
		physDefEffectiveness /= value;
	}
	
	public float getPenEffectiveness() {
		return penEffectiveness;
	}
	
	public void increasePenEffectiveness(float value) {
		penEffectiveness *= value;
	}
	
	public void removePenEffectivenessIncrease(float value) {
		penEffectiveness /= value;
	}
	
	public float getResEffectiveness() {
		return resEffectiveness;
	}
	
	public void increaseResEffectiveness(float value) {
		resEffectiveness *= value;
	}
	
	public void removeResEffectivenessIncrease(float value) {
		resEffectiveness /= value;
	}
	
	public float getSpeedEffectiveness() {
		return speedEffectiveness;
	}
	
	public void increaseSpeedEffectiveness(float value) {
		speedEffectiveness *= value;
	}
	
	public void removeSpeedEffectivenessIncrease(float value) {
		speedEffectiveness /= value;
	}
	
	public float getCritChanceEffectiveness() {
		return critChanceEffectiveness;
	}
	
	public void increaseCritChanceEffectiveness(float value) {
		critChanceEffectiveness *= value;
	}
	
	public void removeCritChanceEffectivenessIncrease(float value) {
		critChanceEffectiveness /= value;
	}
	
	public float getCritMultiplierEffectiveness() {
		return critMultiplierEffectiveness;
	}
	
	public void increaseCritMultiplierEffectiveness(float value) {
		critMultiplierEffectiveness *= value;
	}
	
	public void removeCritMultiplierEffectivenessIncrease(float value) {
		critMultiplierEffectiveness /= value;
	}
	
	public float getDodgeChanceEffectiveness() {
		return dodgeChanceEffectiveness;
	}
	
	public void increaseDodgeChanceEffectiveness(float value) {
		dodgeChanceEffectiveness *= value;
	}
	
	public void removeDodgeChanceEffectivenessIncrease(float value) {
		dodgeChanceEffectiveness /= value;
	}
	
	public float getStabMultiplierEffectiveness() {
		return stabMultiplierEffectiveness;
	}
	
	public void increaseStabMultiplierEffectiveness(float value) {
		stabMultiplierEffectiveness *= value;
	}
	
	public void removeStabMultiplierEffectivenessIncrease(float value) {
		stabMultiplierEffectiveness /= value;
	}
	
	//===========================================
	//         STAT RETRIEVAL METHODS
	//===========================================
	public int calcTotalVitae() {
		int total = StatCalculators.getIntCalculator(totalVitaeCalculator).calculateStat(this);
		
		return total;
	}
	
	public int calcTotalFocus() {
		int total = StatCalculators.getIntCalculator(totalFocusCalculator).calculateStat(this);
		
		return total;
	}
	
	public int calcTotalMagicAtk(BattleContext context) {
		int total = StatCalculators.getIntCalculator(totalMagicAtkCalculator).calculateStat(this);
		
		if(context != null) {
			//Check for magic atk boost to fire types from pyronimbus
			if(context.battleground.weather == WeatherType.PYRONIMBUS) {
				for(ElementType t : base.types) {
					if(t == ElementType.FIRE) {
						total *= PYRONIMBUS_MAGIC_ATK_MULTIPLIER;
						break;
					}
				}
			}
		}
		
		return total;
	}
	
	public int calcTotalMagicDef(BattleContext context) {
		int total = StatCalculators.getIntCalculator(totalMagicDefCalculator).calculateStat(this);
		
		if(context != null) {
			//Check for magic def boost to ground types from sandstorm
			if(context.battleground.weather == WeatherType.SANDSTORM) {
				for(ElementType t : base.types) {
					if(t == ElementType.GROUND) {
						total *= SANDSTORM_MAGIC_DEF_MULTIPLIER;
						break;
					}
				}
			}
		}
		
		return total;
	}
	
	public int calcTotalPhysAtk(BattleContext context) {
		int total = StatCalculators.getIntCalculator(totalPhysAtkCalculator).calculateStat(this);
		
		if(context != null) {
			if(context.battleground.weather == WeatherType.PYRONIMBUS) {
				for(ElementType t : base.types) {
					if(t == ElementType.FIRE) {
						total *= SANDSTORM_MAGIC_DEF_MULTIPLIER;
						break;
					}
				}
			}
		}
		
		return total;
	}
	
	public int calcTotalPhysDef(BattleContext context) {
		int total = StatCalculators.getIntCalculator(totalPhysDefCalculator).calculateStat(this);
		return total;
	}
	
	public int calcTotalPen(BattleContext context) {
		int total = StatCalculators.getIntCalculator(totalPenCalculator).calculateStat(this);
		return total;
	}
	
	public int calcTotalRes(BattleContext context) {
		int total = StatCalculators.getIntCalculator(totalResCalculator).calculateStat(this);
		
		if(context != null) {
			if(context.battleground.weather == WeatherType.LOCUST_SWARM) {
				for(ElementType t : base.types) {
					if(t == ElementType.UNDEAD) {
						total *= LOCUST_SWARM_RES_MULTIPLIER;
						break;
					}
				}
			}
		}
		
		return total;
	}
	
	public int calcTotalSpeed(BattleContext context) {
		int total = StatCalculators.getIntCalculator(totalSpeedCalculator).calculateStat(this);
		
		if(context != null) {
				if(context.battleground.weather == WeatherType.JETSTREAM) {
					for(ElementType t : base.types) {
						if(t == ElementType.AIR) {
							total *= JETSTREAM_SPEED_MULTIPLIER;
							break;
						}
					}
				}
		}
				
		return total;
	}
	
	public float calcTotalCritChance(BattleContext context) {
		float total = StatCalculators.getFloatCalculator(critChanceCalculator).calculateStat(this);
		
		return total;
	}
	
	public float calcTotalCritMultiplier(BattleContext context) {
		float total = StatCalculators.getFloatCalculator(critMultiplieCalculator).calculateStat(this);
		
		return total;
	}
	
	public float calcTotalDodgeChance(BattleContext context) {
		float total = StatCalculators.getFloatCalculator(dodgeChanceCalculator).calculateStat(this);
		
		return total;
	}
	
	public float calcTotalStabMultiplier(BattleContext context) {
		float total = StatCalculators.getFloatCalculator(stabMultiplierCalculator).calculateStat(this);
		
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
		vitaeIncreases = 1;
		focusIncreases = 1;
		magicAtkIncreases = 1;
		magicDefIncreases = 1;
		physAtkIncreases = 1;
		physDefIncreases = 1;
		penIncreases = 1;
		resIncreases = 1;
		speedIncreases = 1;
		
		vitaeEffectiveness = 1f;
		focusEffectiveness = 1f;
		magicAtkEffectiveness = 1f;
		magicDefEffectiveness = 1f;
		physAtkEffectiveness = 1f;
		physDefEffectiveness = 1f;
		penEffectiveness = 1f;
		resEffectiveness = 1f;
		speedEffectiveness = 1f;
	}
	
}
















