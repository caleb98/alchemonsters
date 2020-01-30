package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionChooseRandom implements MoveAction {

	/**
	 * The possible choices that this move action could take.
	 * Each choice has the same probability of being selected.
	 */
	public MoveAction[] choices;
	
	@Override
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		int choice = GameRandom.nextInt(choices.length);
		choices[choice].activate(moveInstance, sourceTeam, opponentTeam);
	}
	
}
