package com.ccode.alchemonsters.combat.effect;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.creature.Creature;

public abstract class Ailment extends Effect implements Json.Serializable {

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
	
	@Override
	public void write(Json json) {
		json.writeValue("name", name);
		json.writeValue("isVisible", isVisible);
		json.writeValue("isStrong", isStrong);
		json.writeValue("duration", duration);
		json.writeValue("lifetime", lifetime);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		//TODO: fix this if needed
	}
	
}
