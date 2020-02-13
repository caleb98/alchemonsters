package com.ccode.alchemonsters.combat.effect;

import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.Subscriber;

public abstract class Effect implements Subscriber, Publisher {

	public final String name;
	public boolean isVisible;
	
	public Effect(String name, boolean isVisible) {
		this.name = name;
		this.isVisible = isVisible;
	}
	
	public abstract boolean needsRemoval();
	
	/**
	 * Called whenever the battle context enters a new state.
	 */
	public void enterState(CombatState state) {};

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
