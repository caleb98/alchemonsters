package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatFinished extends Message {
	
	public static final String ID = "COMBAT_FINISHED";
	
	public final BattleContext context;
	public final CreatureTeam won;
	public final CreatureTeam lost;
	
	public MCombatFinished(BattleContext context, CreatureTeam won, CreatureTeam lost) {
		super(ID);
		this.context = context;
		this.won = won;
		this.lost = lost;
	}
	
}
