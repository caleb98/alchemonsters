package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.UnitController;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatTeamActiveChanged extends Message {

	public static final String ID = "COMBAT_TEAM_ACTIVE_CHANGED";
	
	public final BattleContext context;
	public final BattleTeam team;
	public final int prevActive;
	public final int nextActive;
	
	private MCombatTeamActiveChanged() {
		super(ID);
		context = null;
		team = null;
		prevActive = -1;
		nextActive = -1;
	}
	
	public MCombatTeamActiveChanged(BattleContext context, BattleTeam team, int oldActive, int newActive) {
		super(ID);
		this.context = context;
		this.team = team;
		this.prevActive = oldActive;
		this.nextActive = newActive;
	}
	
}
