package com.ccode.alchemonsters.combat.context;

import java.util.function.IntSupplier;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.UnitController;
import com.ccode.alchemonsters.engine.event.Publisher;

public class BattleEventAction extends BattleEvent implements Publisher {
	
	private UnitController control;
	private int activePos;
	private BattleTeam sourceTeam;
	private BattleTeam opponentTeam;
	
	public BattleEventAction(UnitController control, int activePos, BattleTeam sourceTeam, BattleTeam opponentTeam, IntSupplier speed) {
		super(speed);
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
