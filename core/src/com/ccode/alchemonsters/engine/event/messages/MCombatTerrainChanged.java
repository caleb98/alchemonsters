package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatTerrainChanged extends Message {

	public static final String ID = "COMBAT_TERRAIN_CHANGED";
	
	public final BattleContext context;
	public final Creature source;
	public final String cause;
	public final TerrainType oldTerrain;
	public final TerrainType newTerrain;
	
	private MCombatTerrainChanged() {
		super(ID);
		context = null;
		source = null;
		cause = null;
		oldTerrain = null;
		newTerrain = null;
	}
	
	public MCombatTerrainChanged(BattleContext context, Creature source, String cause, TerrainType oldTerrain, TerrainType newTerrain) {
		super(ID);
		this.context = context;
		this.source = source;
		this.cause = cause;
		this.oldTerrain = oldTerrain;
		this.newTerrain = newTerrain;
	}
	
}
