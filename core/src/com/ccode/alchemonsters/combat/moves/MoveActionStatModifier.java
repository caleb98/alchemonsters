package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.StatType;
import com.ccode.alchemonsters.engine.event.messages.MCombatStatBuffApplied;

public class MoveActionStatModifier implements MoveAction {

	/**
	 * The target of the stat modifier.
	 */
	public MoveActionTarget target;
	/**
	 * Which stat is to be modified.
	 */
	public StatType stat;
	/**
	 * Amount to adjust the stat buff by.
	 */
	public byte amount;
	
	@Override
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		switch(this.target) {
		
		case TARGET:
			for(Creature tar : moveInstance.targets) {
				tar.mods.addMod(amount, stat);
				publish(new MCombatStatBuffApplied(moveInstance.context, moveInstance.source, tar, moveInstance, stat, amount));
			}
			break;
			
		case OPPONENT_TEAM:
			Creature opp;
			for(int i = 0; i < opponentTeam.numActives; ++i) {
				opp = opponentTeam.get(i);
				opp.mods.addMod(amount, stat);
				publish(new MCombatStatBuffApplied(moveInstance.context, moveInstance.source, opp, moveInstance, stat, amount));
			}
			break;
			
		case SELF:
			moveInstance.source.mods.addMod(amount, stat);
			publish(new MCombatStatBuffApplied(moveInstance.context, moveInstance.source, moveInstance.source, moveInstance, stat, amount));
			break;
			
		case SELF_TEAM:
			Creature friendly;
			for(int i = 0; i < sourceTeam.numActives; ++i) {
				friendly = sourceTeam.get(i);
				friendly.mods.addMod(amount, stat);
				publish(new MCombatStatBuffApplied(moveInstance.context, moveInstance.source, friendly, moveInstance, stat, amount));
			}
			break;

		}
	}

}
