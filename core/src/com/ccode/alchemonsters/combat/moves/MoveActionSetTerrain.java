package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.engine.event.messages.MCombatGroundChanged;

public class MoveActionSetTerrain implements MoveAction {

	/**
	 * The type of ground to set 
	 */
	public TerrainType terrain;
	
	@Override
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		TerrainType old = moveInstance.context.battleground.terrain;
		moveInstance.context.battleground.terrain = terrain;
		publish(new MCombatGroundChanged(moveInstance.context, moveInstance.source, moveInstance.move.name, old, terrain));
	}
	
}
