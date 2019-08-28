package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.GroundType;
import com.ccode.alchemonsters.creature.Creature;

public class MoveActionSetGround implements MoveAction {

	/**
	 * The type of ground to set 
	 */
	public GroundType ground;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target,
			CreatureTeam targetTeam) {
		context.battleground.ground = ground;
	}
	
}
