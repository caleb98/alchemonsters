package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;

public abstract class StatusAilmentEffect {

	public final String name;
	
	public StatusAilmentEffect(String name) {
		this.name = name;
	}
	
	public abstract void turnTick(Creature source, Creature target);
	
}
