package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatStateChanged extends Message {

	public static final String ID = "COMBAT_STATE_CHANGED";
	
	public final BattleContext context;
	public final CombatState previous;
	public final CombatState next;
	
	private MCombatStateChanged() {
		super(ID);
		context = null;
		previous = null;
		next = null;
	}
	
	public MCombatStateChanged(BattleContext context, CombatState previous, CombatState next) {
		super(ID);
		this.context = context;
		this.previous = previous;
		this.next = next;
	}
	
}
