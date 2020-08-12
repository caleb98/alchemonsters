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
			c.addCritChanceIncrease(increaseAmount);
			break;
			
		case CRIT_MULTI:
			c.addCritMultiplierIncrease(increaseAmount);
			break;
			
		case DODGE_CHANCE:
			c.addDodgeChanceIncrease(increaseAmount);
			break;
			
		case FOCUS:
			c.addFocusIncrease((int) increaseAmount);
			break;
			
		case MAGIC_ATK:
			c.addMagicAtkIncrease((int) increaseAmount);
			break;
			
		case MAGIC_DEF:
			c.addMagicDefIncrease((int) increaseAmount);
			break;
			
		case PENETRATION:
			c.addPenIncrease((int) increaseAmount);
			break;
			
		case PHYS_ATK:
			c.addPhysAtkIncrease((int) increaseAmount);
			break;
			
		case PHYS_DEF:
			c.addPhysDefIncrease((int) increaseAmount);
			break;
			
		case RESISTANCE:
			c.addResIncrease((int) increaseAmount);
			break;
			
		case SPEED:
			c.addSpeedIncrease((int) increaseAmount);
			break;
			
		case STAB_MULTI:
			c.addStabMultiplierIncrease(increaseAmount);
			break;
			
		case VITAE:
			c.addVitaeIncrease((int) increaseAmount);
			break;
		
		}
	}
	
	@Override
	public void removeFrom(Creature c) {
		switch(type) {

		case CRIT_CHANCE:
			c.removeCritChanceIncrease(increaseAmount);
			break;
			
		case CRIT_MULTI:
			c.removeCritMultiplierIncrease(increaseAmount);
			break;
			
		case DODGE_CHANCE:
			c.removeDodgeChanceIncrease(increaseAmount);
			break;
			
		case FOCUS:
			c.removeFocusIncrease((int) increaseAmount);
			break;
			
		case MAGIC_ATK:
			c.removeMagicAtkIncrease((int) increaseAmount);
			break;
			
		case MAGIC_DEF:
			c.removeMagicDefIncrease((int) increaseAmount);
			break;
			
		case PENETRATION:
			c.removePenIncrease((int) increaseAmount);
			break;
			
		case PHYS_ATK:
			c.removePhysAtkIncrease((int) increaseAmount);
			break;
			
		case PHYS_DEF:
			c.removePhysDefIncrease((int) increaseAmount);
			break;
			
		case RESISTANCE:
			c.removeResIncrease((int) increaseAmount);
			break;
			
		case SPEED:
			c.removeSpeedIncrease((int) increaseAmount);
			break;
			
		case STAB_MULTI:
			c.removeStabMultiplierIncrease(increaseAmount);
			break;
			
		case VITAE:
			c.removeVitaeIncrease((int) increaseAmount);
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
