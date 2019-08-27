package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatHealingReceived extends Message {

	public static final String ID = "COMBAT_HEALING_RECEIVED";
	
	public final BattleContext context;
	public final Creature source;
	public final Creature target;
	/**
	 * The thing that caused the healing (name of the move, ailment, etc.)
	 */
	public final String cause;
	public final int amount;
	public final boolean isTriggered;
	
	public MCombatHealingReceived(BattleContext context, Creature source, Creature target, String cause, int amount, boolean isTriggered) {
		super(ID);
		this.context = context;
		this.source = source;
		this.target = target;
		this.cause = cause;
		this.amount = amount;
		this.isTriggered = isTriggered;
	}
	
}
