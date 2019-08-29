package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.GroundType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatGroundChanged extends Message {

	public static final String ID = "COMBAT_GROUND_CHANGED";
	
	public final BattleContext context;
	public final Creature source;
	public final String cause;
	public final GroundType oldGround;
	public final GroundType newGround;
	
	public MCombatGroundChanged(BattleContext context, Creature source, String cause, GroundType oldGround, GroundType newGround) {
		super(ID);
		this.context = context;
		this.source = source;
		this.cause = cause;
		this.oldGround = oldGround;
		this.newGround = newGround;
	}
	
}
