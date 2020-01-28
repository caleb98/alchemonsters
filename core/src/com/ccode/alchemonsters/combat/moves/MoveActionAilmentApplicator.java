package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionAilmentApplicator implements MoveAction {
	
	/**
	 * The target of this move action.
	 */
	public MoveActionTarget target;
	/**
	 * The name of the ailment to apply.
	 */
	private String ailmentName;
	/**
	 * The chance that the ailment is applied. Defaults to 1.
	 */
	public float chance = 1f;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target, BattleTeam opponentTeam) {
		if(!(GameRandom.nextFloat() <= chance)) {
			return;
		}
		
		//TODO: proper ailment application
		switch(this.target) {
		
		case TARGET:
			
			break;
			
		case OPPONENT_TEAM:
			break;
			
		case SELF:
			break;
			
		case SELF_TEAM:
			break;
		
		}
	}

}
