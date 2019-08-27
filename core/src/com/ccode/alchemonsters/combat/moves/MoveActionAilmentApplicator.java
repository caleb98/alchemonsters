package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.ailments.StatusAilment;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionAilmentApplicator implements MoveAction {
	
	/**
	 * The target of this move action.
	 */
	public MoveTarget target;
	/**
	 * The name of the ailment to apply.
	 */
	private String ailmentName;
	/**
	 * The chance that the ailment is applied. Defaults to 1.
	 */
	public float chance = 1f;
	
	public MoveActionAilmentApplicator() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target, CreatureTeam targetTeam) {
		if(!(GameRandom.nextFloat() <= chance)) {
			return;
		}
		
		if(target.currentAilment != null) {
			target.currentAilment.remove();
		}
		
		target.currentAilment = new StatusAilment(context, source, target, move.name, ailmentName);
		target.currentAilment.apply();
	}

}
