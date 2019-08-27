package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
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
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target, CreatureTeam targetTeam) {
		switch(this.target) {
		
		case OPPONENT:
			target.buffs.addBuff(amount, stat);
			publish(new MCombatStatBuffApplied(context, source, target, move, stat, amount));
			break;
			
		case OPPONENT_TEAM:
			for(Creature c : targetTeam.creatures) {
				c.buffs.addBuff(amount, stat);
				publish(new MCombatStatBuffApplied(context, source, c, move, stat, amount));
			}
			break;
			
		case SELF:
			source.buffs.addBuff(amount, stat);
			publish(new MCombatStatBuffApplied(context, source, source, move, stat, amount));
			break;
			
		case SELF_TEAM:
			for(Creature c : sourceTeam.creatures) {
				c.buffs.addBuff(amount, stat);
				publish(new MCombatStatBuffApplied(context, source, c, move, stat, amount));
			}
			break;

		}
	}

}
