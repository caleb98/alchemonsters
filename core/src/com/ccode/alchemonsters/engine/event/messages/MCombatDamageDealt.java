package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatDamageDealt extends Message {

	public static final String ID = "COMBAT_DAMAGE_DEALT";
	
	public final BattleContext context;
	public final Creature source;
	public final Creature target;
	public final Move move;
	public final int amount;
	public final boolean isHit;
	public final boolean isCrit;
	
	public MCombatDamageDealt(BattleContext context, Creature source, Creature target, Move move,
							  int amount, boolean isHit, boolean isCrit) {
		super(ID);
		this.context = context;
		this.source = source;
		this.target = target;
		this.move = move;
		this.amount = amount;
		this.isHit = isHit;
		this.isCrit = isCrit;
	}
	
}
