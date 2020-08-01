package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.moves.MoveInstance;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatMovePreCast extends Message {

	public static final String ID = "COMBAT_MOVE_PRE_CAST";
	
	public final MoveInstance moveInstance;
	
	private MCombatMovePreCast() {
		super(ID);
		moveInstance = null;
	}
	
	public MCombatMovePreCast(MoveInstance moveInstance) {
		super(ID);
		this.moveInstance = moveInstance;
	}
	
}
