package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;

public abstract class StatusAilment {

	public Creature source;
	public Creature target;
	
	public StatusAilment(Creature source, Creature target) {
		this.source = source;
		this.target = target;
	}

	public abstract void turnTick();
	
}
