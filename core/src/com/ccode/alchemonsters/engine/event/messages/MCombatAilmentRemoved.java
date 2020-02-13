package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatAilmentRemoved extends Message {
	
	public static final String ID = "COMBAT_AILMENT_REMOVED";

	public final BattleContext context;
	public final Creature source;
	public final Creature target;
	public final String cause;
	public final String ailmentName;
	
	private MCombatAilmentRemoved() {
		super(ID);
		context = null;
		source = null;
		target = null;
		cause = null;
		ailmentName = null;
	}
	
	public MCombatAilmentRemoved(BattleContext context, Creature source, Creature target, String cause, String ailmentName) {
		super(ID);
		this.context = context;
		this.source = source;
		this.target = target;
		this.cause = cause;
		this.ailmentName = ailmentName;
	}
	
}
