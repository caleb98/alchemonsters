package com.ccode.alchemonsters.creature.equip;

import com.ccode.alchemonsters.creature.Creature;

public interface Affix {

	public void applyTo(Creature c);
	public void removeFrom(Creature c);
	
	public String getSimpleDescription();
	public String getDetailedDescription();
	
}
