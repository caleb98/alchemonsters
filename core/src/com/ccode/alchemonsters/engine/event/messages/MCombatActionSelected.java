package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatActionSelected extends Message {

	public static final String ID = "COMBAT_ACTION_SELECTED";

	public final BattleContext context;
	public final BattleAction selected;
	
	public MCombatActionSelected(BattleContext context, BattleAction selected) {
		super(ID);
		this.context = context;
		this.selected = selected;
	}
	
}
