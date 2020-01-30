package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
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
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		action.activate(moveInstance, sourceTeam, opponentTeam);
		for(int i = 1; i < repeatTimes; ++i) {
			if(GameRandom.nextFloat() < repeatChance) {
				action.activate(moveInstance, sourceTeam, opponentTeam);
			}
			else {
				return;
			}
		}
	}
	
}
