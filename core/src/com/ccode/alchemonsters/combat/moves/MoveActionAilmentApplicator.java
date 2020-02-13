package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.database.AilmentDatabase;
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
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		if(!(GameRandom.nextFloat() <= chance)) {
			return;
		}
		
		switch(this.target) {
		
		case TARGET:
			for(Creature tar : moveInstance.targets) {
				tar.addEffect(AilmentDatabase.getEffect(ailmentName));
			}
			break;
			
		case OPPONENT_TEAM:
			for(int i = 0; i < opponentTeam.numActives; ++i) {
				Creature tar = opponentTeam.get(i);
				tar.addEffect(AilmentDatabase.getEffect(ailmentName));
			}
			break;
			
		case SELF:
			moveInstance.source.addEffect(AilmentDatabase.getEffect(ailmentName));
			break;
			
		case SELF_TEAM:
			for(int i = 0; i < sourceTeam.numActives; ++i) {
				Creature tar = sourceTeam.get(i);
				tar.addEffect(AilmentDatabase.getEffect(ailmentName));
			}
			break;
		
		}
	}

}
