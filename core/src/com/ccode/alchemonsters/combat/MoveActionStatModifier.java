package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.StatType;

public class MoveActionStatModifier implements IMoveAction {

	public MoveTarget target;
	public StatType stat;
	public int amt;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, Creature target) {
		
	}

}
