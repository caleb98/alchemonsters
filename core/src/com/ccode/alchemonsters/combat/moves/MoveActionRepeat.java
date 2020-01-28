package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionRepeat implements MoveAction {

	/**
	 * Which action to be repeated.
	 */
	public MoveAction action;
	/**
	 *The number of times to repeat the action.
	 */
	public int repeatTimes;
	/**
	 * The chance that a repeat occurs after the first
	 * activation. (If the roll for a repeat does not
	 * succeed, no further repeats will occur regardless
	 * of whether or not the total "repeatTimes" variable
	 * has been reached.)
	 */
	public float repeatChance = 1f;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target, BattleTeam opponentTeam) {
		action.activate(move, context, source, sourceTeam, target, opponentTeam);
		for(int i = 1; i < repeatTimes; ++i) {
			if(GameRandom.nextFloat() < repeatChance) {
				action.activate(move, context, source, sourceTeam, target, opponentTeam);
			}
			else {
				return;
			}
		}
	}
	
}
