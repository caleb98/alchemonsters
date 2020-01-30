package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.moves.MoveInstance;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatChargeFinished extends Message {

	public static final String ID = "COMBAT_CHARGE_FINISHED";
	
	public final MoveInstance moveInstance;
	
	private MCombatChargeFinished() {
		super(ID);
		moveInstance = null;
	}
	
	public MCombatChargeFinished(MoveInstance moveInstance) {
		super(ID);
		this.moveInstance = moveInstance;
	}
	
}
