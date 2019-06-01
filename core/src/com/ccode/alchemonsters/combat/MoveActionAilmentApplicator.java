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
	}

}
