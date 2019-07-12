package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatDamageDealt extends Message {

	public static final String ID = "COMBAT_DAMAGE_DEALT";
	
	public final BattleContext context;
	public final Creature source;
	public final Creature target;
	/**
	 * The thing that caused the damage (name of move, ailment, etc.)
	 */
	public final String cause;
	public final ElementType elementType;
	public final int amount;
	public final boolean isHit;
	public final boolean isCrit;
	/**
	 * Whether or not this damage was a triggered effect.
	 */
	public final boolean isTriggered;
	
	public MCombatDamageDealt(BattleContext context, Creature source, Creature target, String cause,
							  ElementType elementType, int amount, boolean isHit, boolean isCrit, boolean isTriggered) {
		super(ID);
		this.context = context;
		this.source = source;
		this.target = target;
		this.cause = cause;
		this.elementType = elementType;
		this.amount = amount;
		this.isHit = isHit;
		this.isCrit = isCrit;
		this.isTriggered = isTriggered;
	}
	
}
