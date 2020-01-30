package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.moves.MoveInstance;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatChargeStarted extends Message {

	public static final String ID = "COMBAT_CHARGE_STARTED";
	
	public final MoveInstance moveInstance;
	
	private MCombatChargeStarted() {
		super(ID);
		moveInstance = null;
	}
	
	public MCombatChargeStarted(MoveInstance moveInstance) {
		super(ID);
		this.moveInstance = moveInstance;
	}
	
}
