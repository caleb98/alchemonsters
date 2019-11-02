package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.GroundType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.messages.MCombatGroundChanged;

public class MoveActionSetGround implements MoveAction {

	/**
	 * The type of ground to set 
	 */
	public GroundType ground;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target,
			BattleTeam targetTeam) {
		GroundType old = context.battleground.ground;
		context.battleground.ground = ground;
		publish(new MCombatGroundChanged(context, source, move.name, old, ground));
	}
	
}
