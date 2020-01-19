package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.BiomeType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.messages.MCombatTerrainChanged;

public class MoveActionSetTerrain implements MoveAction {

	/**
	 * Which terrain to set the battleground to. 
	 */
	public BiomeType terrain;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target,
			BattleTeam targetTeam) {
		BiomeType old = context.battleground.biome;
		context.battleground.biome = terrain;
		publish(new MCombatTerrainChanged(context, source, move.name, old, terrain));
	}
	
}
