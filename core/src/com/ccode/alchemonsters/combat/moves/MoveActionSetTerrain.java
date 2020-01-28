package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.messages.MCombatGroundChanged;

public class MoveActionSetTerrain implements MoveAction {

	/**
	 * The type of ground to set 
	 */
	public TerrainType terrain;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target, BattleTeam opponentTeam) {
		TerrainType old = context.battleground.terrain;
		context.battleground.terrain = terrain;
		publish(new MCombatGroundChanged(context, source, move.name, old, terrain));
	}
	
}
