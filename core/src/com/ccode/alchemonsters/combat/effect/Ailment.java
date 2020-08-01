package com.ccode.alchemonsters.combat.effect;

import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.creature.Creature;

public abstract class Ailment extends Effect {

	/**
	 * Whether or not this ailment is a "strong" ailment;
	 * that is, whether or not this ailment should take up
	 * the "strong" ailment slot on a creature.
	 */
	public final boolean isStrong;
	
	public final int duration;
	private int lifetime;
	
	protected Creature appliedTo;
	
	public Ailment(String name, boolean isStrong, int duration) {
		super(name, true);
		this.isStrong = isStrong;
		this.duration = duration;
	}
	
	@Override
	public void onApply(Creature c) {
		appliedTo = c;
	}
	
	@Override
	public boolean needsRemoval() {
		return lifetime >= duration;
	}
	
	@Override
	public void enterState(BattleContext context, CombatState state) {
		if(state == CombatState.END_PHASE) {
			lifetime += 1;
		}
	}
	
}
