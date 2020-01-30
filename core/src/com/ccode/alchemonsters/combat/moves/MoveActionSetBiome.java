package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.BiomeType;
import com.ccode.alchemonsters.engine.event.messages.MCombatTerrainChanged;

public class MoveActionSetBiome implements MoveAction {

	/**
	 * Which terrain to set the battleground to. 
	 */
	public BiomeType biome;
	
	@Override
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		BiomeType old = moveInstance.context.battleground.biome;
		moveInstance.context.battleground.biome = biome;
		publish(new MCombatTerrainChanged(moveInstance.context, moveInstance.source, moveInstance.move.name, old, biome));
	}
	
}
