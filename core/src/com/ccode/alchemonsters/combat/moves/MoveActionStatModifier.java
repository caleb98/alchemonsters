package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.StatType;
import com.ccode.alchemonsters.engine.event.messages.MCombatStatBuffApplied;

public class MoveActionStatModifier implements MoveAction {

	/**
	 * The target of the stat modifier.
	 */
	public MoveTarget target;
	/**
	 * Which stat is to be modified.
	 */
	public StatType stat;
	/**
	 * Amount to adjust the stat buff by.
	 */
	public byte amount;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target, BattleTeam targetTeam) {
		switch(this.target) {
		
		case OPPONENT:
			target.mods.addMod(amount, stat);
			publish(new MCombatStatBuffApplied(context, source, target, move, stat, amount));
			break;
			
		case OPPONENT_TEAM:
			break;
			
		case SELF:
			source.mods.addMod(amount, stat);
			publish(new MCombatStatBuffApplied(context, source, source, move, stat, amount));
			break;
			
		case SELF_TEAM:
			break;

		}
	}

}
