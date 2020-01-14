package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatGroundChanged extends Message {

	public static final String ID = "COMBAT_GROUND_CHANGED";
	
	public final BattleContext context;
	public final Creature source;
	public final String cause;
	public final TerrainType oldGround;
	public final TerrainType newGround;
	
	public MCombatGroundChanged() {
		super(ID);
		context = null;
		source = null;
		cause = null;
		oldGround = null;
		newGround = null;
	}
	
	public MCombatGroundChanged(BattleContext context, Creature source, String cause, TerrainType oldGround, TerrainType newGround) {
		super(ID);
		this.context = context;
		this.source = source;
		this.cause = cause;
		this.oldGround = oldGround;
		this.newGround = newGround;
	}
	
}
