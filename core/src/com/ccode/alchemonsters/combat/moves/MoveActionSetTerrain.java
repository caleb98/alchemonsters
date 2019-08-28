package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.creature.Creature;

public class MoveActionSetTerrain implements MoveAction {

	/**
	 * Which terrain to set the battleground to. 
	 */
	public TerrainType terrain;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target,
			CreatureTeam targetTeam) {
		context.battleground.terrain = terrain;
	}
	
}
