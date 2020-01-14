package com.ccode.alchemonsters.creature.equip;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.StatType;

public class StatIncreaseAffix implements Affix {

	private StatType type;
	private float increaseAmount;
	
	public StatIncreaseAffix(StatType type, float amt) {
		increaseAmount = amt;
	}
	
	@Override
	public void applyTo(Creature c) {
		switch(type) {

		case CRIT_CHANCE:
			c.critChance += increaseAmount;
			break;
			
		case CRIT_MULTI:
			c.critMultiplier += increaseAmount;
			break;
			
		case DODGE_CHANCE:
			c.dodgeChance += increaseAmount;
			break;
			
		case FLINCH_CHANCE:
			c.flinchChance += increaseAmount;
			break;
			
		case FOCUS:
			c.focusIncreases += increaseAmount;
			break;
			
		case MAGIC_ATK:
			c.magicAtkIncreases += increaseAmount;
			break;
			
		case MAGIC_DEF:
			c.magicDefIncreases += increaseAmount;
			break;
			
		case PENETRATION:
			c.penIncreases += increaseAmount;
			break;
			
		case PHYS_ATK:
			c.physAtkIncreases += increaseAmount;
			break;
			
		case PHYS_DEF:
			c.physDefIncreases += increaseAmount;
			break;
			
		case RESISTANCE:
			c.resIncreases += increaseAmount;
			break;
			
		case SPEED:
			c.speedIncreases += increaseAmount;
			break;
			
		case STAB_MULTI:
			c.stabMultiplier += increaseAmount;
			break;
			
		case VITAE:
			c.vitaeIncreases += increaseAmount;
			break;
		
		}
	}
	
	@Override
	public void removeFrom(Creature c) {
		switch(type) {

		case CRIT_CHANCE:
			c.critChance -= increaseAmount;
			break;
			
		case CRIT_MULTI:
			c.critMultiplier -= increaseAmount;
			break;
			
		case DODGE_CHANCE:
			c.dodgeChance -= increaseAmount;
			break;
			
		case FLINCH_CHANCE:
			c.flinchChance -= increaseAmount;
			break;
			
		case FOCUS:
			c.focusIncreases -= increaseAmount;
			break;
			
		case MAGIC_ATK:
			c.magicAtkIncreases -= increaseAmount;
			break;
			
		case MAGIC_DEF:
			c.magicDefIncreases -= increaseAmount;
			break;
			
		case PENETRATION:
			c.penIncreases -= increaseAmount;
			break;
			
		case PHYS_ATK:
			c.physAtkIncreases -= increaseAmount;
			break;
			
		case PHYS_DEF:
			c.physDefIncreases -= increaseAmount;
			break;
			
		case RESISTANCE:
			c.resIncreases -= increaseAmount;
			break;
			
		case SPEED:
			c.speedIncreases -= increaseAmount;
			break;
			
		case STAB_MULTI:
			c.stabMultiplier -= increaseAmount;
			break;
			
		case VITAE:
			c.vitaeIncreases -= increaseAmount;
			break;
		
		}
	}
	
	@Override
	public String getSimpleDescription() {
		return String.format(
				"%s%.0f%% to %s", 
				(increaseAmount >= 0) ? "+" : "-",
				increaseAmount,
				type.displayName);
	}
	
	@Override
	public String getDetailedDescription() {
		//TODO: correct text for detailed desc.
		return getSimpleDescription();
	}
	
}
