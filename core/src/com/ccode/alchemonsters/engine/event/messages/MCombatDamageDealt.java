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
	public final boolean isCrit;
	public final boolean isStab;
	/**
	 * Whether or not this damage was a triggered effect.
	 */
	public final boolean isTriggered;
	
	private MCombatDamageDealt() {
		super(ID);
		context = null;
		source = null;
		target = null;
		cause = null;
		elementType = null;
		amount = -1;
		isCrit = false;
		isStab = false;
		isTriggered = false;
	}
	
	public MCombatDamageDealt(BattleContext context, Creature source, Creature target, String cause,
							  ElementType elementType, int amount, boolean isCrit, boolean isStab, boolean isTriggered) {
		super(ID);
		this.context = context;
		this.source = source;
		this.target = target;
		this.cause = cause;
		this.elementType = elementType;
		this.amount = amount;
		this.isCrit = isCrit;
		this.isStab = isStab;
		this.isTriggered = isTriggered;
	}
	
}
