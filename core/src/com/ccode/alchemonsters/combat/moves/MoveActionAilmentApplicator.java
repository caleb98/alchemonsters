package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.database.AilmentDatabase;
import com.ccode.alchemonsters.engine.event.messages.MCombatAilmentApplied;
import com.ccode.alchemonsters.engine.event.messages.MConsoleMessage;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionAilmentApplicator implements MoveAction {
	
	/**
	 * The target of this move action.
	 */
	public MoveActionTarget target;
	/**
	 * The name of the ailment to apply.
	 */
	public String ailmentName;
	/**
	 * The number of rounds the ailment should stay active for
	 */
	public int duration;
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
				if(!tar.hasAilment(ailmentName)) {
					tar.addAilment(AilmentDatabase.getAilment(ailmentName, duration));
					publish(new MCombatAilmentApplied(moveInstance.context, moveInstance.source, tar, moveInstance.move.name, ailmentName));
				}
				else {
					publish(new MConsoleMessage(
							String.format("Attempted to apply %s to %s, but they already had the ailment active!", 
									ailmentName,
									tar.personalName)
					));
				}
			}
			break;
			
		case OPPONENT_TEAM:
			for(int i = 0; i < opponentTeam.numActives; ++i) {
				Creature tar = opponentTeam.get(i);
				if(!tar.hasAilment(ailmentName)) {
					tar.addAilment(AilmentDatabase.getAilment(ailmentName, duration));
					publish(new MCombatAilmentApplied(moveInstance.context, moveInstance.source, tar, moveInstance.move.name, ailmentName));
				}
				else {
					publish(new MConsoleMessage(
							String.format("Attempted to apply %s to %s, but they already had the ailment active!", 
									ailmentName,
									tar.personalName)
					));
				}
			}
			break;
			
		case SELF:
			if(!moveInstance.source.hasAilment(ailmentName)) {
				moveInstance.source.addAilment(AilmentDatabase.getAilment(ailmentName, duration));
				publish(new MCombatAilmentApplied(moveInstance.context, moveInstance.source, moveInstance.source, moveInstance.move.name, ailmentName));
			}
			else {
				publish(new MConsoleMessage(
						String.format("Attempted to apply %s to %s, but they already had the ailment active!", 
								ailmentName,
								moveInstance.source.personalName)
				));
			}
			break;
			
		case SELF_TEAM:
			for(int i = 0; i < sourceTeam.numActives; ++i) {
				Creature tar = sourceTeam.get(i);
				if(!tar.hasAilment(ailmentName)) {
					tar.addAilment(AilmentDatabase.getAilment(ailmentName, duration));
					publish(new MCombatAilmentApplied(moveInstance.context, moveInstance.source, tar, moveInstance.move.name, ailmentName));
				}
				else {
					publish(new MConsoleMessage(
							String.format("Attempted to apply %s to %s, but they already had the ailment active!", 
									ailmentName,
									tar.personalName)
					));
				}
			}
			break;
		
		}
	}

}
