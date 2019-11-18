package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.StatType;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatStatBuffApplied extends Message {

	public static final String ID = "COMBAT_STAT_BUFF_APPLIED";
	
	public final BattleContext context;
	public final Creature source;
	public final Creature target;
	public final Move move;
	public final StatType statBuffed;
	public final int buffAmt;
	
	private MCombatStatBuffApplied() {
		super(ID);
		context = null;
		source = null;
		target = null;
		move = null;
		statBuffed = null;
		buffAmt = -1;
	}
	
	public MCombatStatBuffApplied(BattleContext context, Creature source, Creature target, Move move,
								  StatType statBuffed, int buffAmt) {
		super(ID);
		this.context = context;
		this.source = source;
		this.target = target;
		this.move = move;
		this.statBuffed = statBuffed;
		this.buffAmt = buffAmt;
	}
	

	
}
