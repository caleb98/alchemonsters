package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionAilmentApplicator implements IMoveAction {
	
	public MoveTarget target;
	public float chance;
	public int duration;
	public String ailmentName;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, Creature target) {
		if(!(GameRandom.nextFloat() <= chance)) {
			return;
		}
		
		switch(this.target) {
		
		case SELF:
			source.ailments.add(new StatusAilment(ailmentName, source, source, duration));
			break;
			
		case SELF_TEAM:
			CreatureTeam sourceTeam = null;
			for(Creature c : context.teamA.creatures) {
				if(c == source) {
					sourceTeam = context.teamA;
					break;
				}
			}
			if(sourceTeam == null) {
				sourceTeam = context.teamB;
			}
			for(Creature c : sourceTeam.creatures) {
				c.ailments.add(new StatusAilment(ailmentName, source, c, duration));
			}
			break;
			
		case OPPONENT:
			target.ailments.add(new StatusAilment(ailmentName, source, target, duration));
			break;
			
		case OPPONENT_TEAM:
			CreatureTeam targetTeam = null;
			for(Creature c : context.teamA.creatures) {
				if(c == target) {
					targetTeam = context.teamA;
					break;
				}
			}
			if(targetTeam == null) {
				targetTeam = context.teamB;
			}
			for(Creature c : targetTeam.creatures) {
				c.ailments.add(new StatusAilment(ailmentName, source, c, duration));
			}
			break;
		
		}
	}

}
