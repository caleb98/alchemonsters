package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.Creature;

public class MoveActionDamage implements IMoveAction {
	
	private int amount;
	private MoveType type;
	
	public MoveActionDamage(MoveType type) {
		this.type = type;
	}
	
	public void activate(Move move, BattleContext context, Creature source, Creature target) {
		
	}
	
}
