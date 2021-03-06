package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatStarted extends Message {

	public static final String ID = "COMBAT_STARTED";
	
	public final BattleContext context;
	public final BattleTeam teamA;
	public final BattleTeam teamB;
	
	private MCombatStarted() {
		super(ID);
		context = null;
		teamA = null;
		teamB = null;
	}
	
	public MCombatStarted(BattleContext context) {
		super(ID);
		this.context = context;
		this.teamA = context.teamA;
		this.teamB = context.teamB;
	}
	
}
