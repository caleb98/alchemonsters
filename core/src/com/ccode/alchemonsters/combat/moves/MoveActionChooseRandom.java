package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionChooseRandom implements MoveAction {

	/**
	 * The possible choices that this move action could take.
	 * Each choice has the same probability of being selected.
	 */
	public MoveAction[] choices;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target, BattleTeam targetTeam) {
		int choice = GameRandom.nextInt(choices.length);
		choices[choice].activate(move, context, source, sourceTeam, target, targetTeam);
	}
	
}
