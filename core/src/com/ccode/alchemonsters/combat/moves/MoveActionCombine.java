package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.creature.Creature;

public class MoveActionCombine implements MoveAction {

	/**
	 * The actions to be run.
	 */
	public MoveAction[] actions;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target, BattleTeam targetTeam) {
		for(MoveAction a : actions) {
			a.activate(move, context, source, sourceTeam, target, targetTeam);
		}
	}
	
}
