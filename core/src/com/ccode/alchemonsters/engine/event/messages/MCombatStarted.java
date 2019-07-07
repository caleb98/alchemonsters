package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatStarted extends Message {

	public static final String ID = "COMBAT_STARTED";
	
	public final BattleContext context;
	public final CreatureTeam teamA;
	public final CreatureTeam teamB;
	
	public MCombatStarted(BattleContext context, CreatureTeam teamA, CreatureTeam teamB) {
		super(ID);
		this.context = context;
		this.teamA = teamA;
		this.teamB = teamB;
	}
	
}
