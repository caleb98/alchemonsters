package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;

public class StatusAilment {

	public StatusAilmentEffect effect;
	public Creature source;
	public Creature target;
	public int duration;
	
	public StatusAilment(String ailment, Creature source, Creature target, int duration) {
		this.source = source;
		this.target = target;
		this.duration = duration;
		
		effect = StatusAilmentDictionary.getAilment(ailment);
	}
	
	public void turnTick() {
		effect.startPhase(source, target);
		duration -= 1;
	}
	
	public boolean isExpired() {
		return duration <= 0;
	}
	
}
