package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleController;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatTeamActiveChanged extends Message {

	public static final String ID = "COMBAT_TEAM_ACTIVE_CHANGED";
	
	public final BattleContext context;
	public final BattleController control;
	public final CreatureTeam team;
	public final int prevActive;
	public final int nextActive;
	
	public MCombatTeamActiveChanged(BattleContext context, BattleController control, CreatureTeam team, int oldActive, int newActive) {
		super(ID);
		this.context = context;
		this.control = control;
		this.team = team;
		this.prevActive = oldActive;
		this.nextActive = newActive;
	}
	
}
