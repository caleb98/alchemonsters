package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.StatType;
import com.ccode.alchemonsters.engine.event.messages.MCombatStatBuffApplied;

public class MoveActionStatModifier implements MoveAction {

	public MoveTarget target;
	public StatType stat;
	public byte amt;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target, CreatureTeam targetTeam) {
		switch(this.target) {
		
		case OPPONENT:
			target.buffs.addBuff(amt, stat);
			publish(new MCombatStatBuffApplied(context, source, target, move, stat, amt));
			break;
			
		case OPPONENT_TEAM:
			for(Creature c : targetTeam.creatures) {
				c.buffs.addBuff(amt, stat);
				publish(new MCombatStatBuffApplied(context, source, c, move, stat, amt));
			}
			break;
			
		case SELF:
			source.buffs.addBuff(amt, stat);
			publish(new MCombatStatBuffApplied(context, source, source, move, stat, amt));
			break;
			
		case SELF_TEAM:
			for(Creature c : sourceTeam.creatures) {
				c.buffs.addBuff(amt, stat);
				publish(new MCombatStatBuffApplied(context, source, c, move, stat, amt));
			}
			break;

		}
	}

}
