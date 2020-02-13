package com.ccode.alchemonsters.combat.context;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.UnitController;
import com.ccode.alchemonsters.engine.event.Publisher;

public class BattleEventAction implements BattleEvent, Publisher {
	
	private UnitController control;
	private int activePos;
	private BattleTeam sourceTeam;
	private BattleTeam opponentTeam;
	
	public BattleEventAction(UnitController control, int activePos, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		this.control = control;
		this.activePos = activePos;
		this.sourceTeam = sourceTeam;
		this.opponentTeam = opponentTeam;
	}
	
	@Override
	public void runEvent(BattleContext context) {
		context.doBattleAction(control, activePos, sourceTeam, opponentTeam);
	}	
	
}
