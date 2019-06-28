package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatStateChanged extends Message {

	public static final String ID = "COMBAT_STATE_CHANGED";
	
	public final BattleContext context;
	public final CombatState previous;
	public final CombatState next;
	
	public MCombatStateChanged(BattleContext context, CombatState previous, CombatState next) {
		super(ID);
		this.context = context;
		this.previous = previous;
		this.next = next;
	}
	
}
