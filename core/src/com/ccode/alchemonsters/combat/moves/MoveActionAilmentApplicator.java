package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionAilmentApplicator implements MoveAction {
	
	public MoveTarget target;
	public float chance;
	public int duration;
	public String ailmentName;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target, CreatureTeam targetTeam) {
		if(!(GameRandom.nextFloat() <= chance)) {
			return;
		}
	}

}
