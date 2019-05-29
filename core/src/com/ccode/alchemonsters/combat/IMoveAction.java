package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;

public interface IMoveAction {
	public void activate(Move move, BattleContext context, Creature source, Creature target);
}
