package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;

public class MoveActionCombine implements MoveAction {

	/**
	 * The actions to be run.
	 */
	public MoveAction[] actions;
	
	@Override
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		for(MoveAction a : actions) {
			a.activate(moveInstance, sourceTeam, opponentTeam);
		}
	}
	
}
