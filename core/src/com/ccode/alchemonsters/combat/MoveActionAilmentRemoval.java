package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;

public class MoveActionAilmentRemoval implements IMoveAction {

	private MoveTarget target;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, Creature target) {
		
	}
	
	private enum RemovalType {
		
	}

}
