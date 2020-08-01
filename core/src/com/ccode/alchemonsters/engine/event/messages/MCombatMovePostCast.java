package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.moves.MoveInstance;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatMovePostCast extends Message {

	public static final String ID = "COMBAT_MOVE_POST_CAST";
	
	public final MoveInstance moveInstance;
	
	private MCombatMovePostCast() {
		super(ID);
		moveInstance = null;
	}
	
	public MCombatMovePostCast(MoveInstance moveInstance) {
		super(ID);
		this.moveInstance = moveInstance;
	}
	
}
