package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionChance implements MoveAction {

	/**
	 * The action to run if the roll succeeds.
	 */
	public MoveAction action;
	/**
	 * The chance that the embedded action should be run.
	 */
	public float chance;
	
	@Override
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		if(GameRandom.nextFloat() < chance) {
			action.activate(moveInstance, sourceTeam, opponentTeam);
		}
	}
	
}
