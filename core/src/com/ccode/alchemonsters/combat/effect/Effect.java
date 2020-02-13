package com.ccode.alchemonsters.combat.effect;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.Subscriber;

public abstract class Effect implements Subscriber, Publisher {

	private String name;
	private boolean isVisible;
	
	public Effect(String name, boolean isVisible) {
		this.name = name;
		this.isVisible = isVisible;
	}

	/**
	 * Called when this effect is applied to a creature.
	 * @param c creature effect is applied to
	 */
	public void onApply(Creature c) {}
	
	/**
	 * Called when this effect is removed from a creature.
	 * @param c creature effect is removed from
	 */
	public void onRemove(Creature c) {}
	
}
