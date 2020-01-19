package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.messages.MCombatGroundChanged;

public class MoveActionSetGround implements MoveAction {

	/**
	 * The type of ground to set 
	 */
	public TerrainType ground;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target,
			BattleTeam targetTeam) {
		TerrainType old = context.battleground.terrain;
		context.battleground.terrain = ground;
		publish(new MCombatGroundChanged(context, source, move.name, old, ground));
	}
	
}
