package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatChargeStarted extends Message {

	public static final String ID = "COMBAT_CHARGE_STARTED";
	
	public final BattleContext context;
	public final Creature source;
	public final Creature target;
	public final Move chargingMove;
	
	private MCombatChargeStarted() {
		super(ID);
		context = null;
		source = null;
		target = null;
		chargingMove = null;
	}
	
	public MCombatChargeStarted(BattleContext context, Creature source, Creature target, Move chargingMove) {
		super(ID);
		this.context = context;
		this.source = source;
		this.target = target;
		this.chargingMove = chargingMove;
	}
	
}
