package com.ccode.alchemonsters.combat.effect;

import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.combat.context.BattleEventDamage;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;

public class ScorchAilment extends Ailment {

	public ScorchAilment(int duration) {
		super("Scorch", false, duration);
	}
	
	@Override
	public void onApply(Creature c) {
		super.onApply(c);
		
		subscribe(MCombatDamageDealt.ID);
	}
	
	@Override
	public void onRemove(Creature c) {
		super.onRemove(c);
		
		unsubscribe(MCombatDamageDealt.ID);
	}
	
	@Override
	public void enterState(BattleContext context, CombatState state) {
		super.enterState(context, state);
		
		if(state == CombatState.END_PHASE) {
			int scorchDamage = (int) (appliedTo.maxHealth / 16f);
			context.addBattleEvent(new BattleEventDamage(
					null, 
					name, 
					appliedTo, 
					ElementType.FIRE, 
					scorchDamage, 
					false, 
					false, 
					false //This damage is not triggered because it occurs at a specified moment in the game turn cycle
			));
		}
	}
	
	@Override
	public void handleMessage(Message currentMessage) {
		if(currentMessage instanceof MCombatDamageDealt) {
			MCombatDamageDealt full = (MCombatDamageDealt) currentMessage;
			if(full.target == appliedTo) {
				int scorchDamage = (int) (appliedTo.maxHealth / 16f);
				full.context.addBattleEvent(new BattleEventDamage(
						null,
						name,
						appliedTo,
						ElementType.FIRE,
						scorchDamage,
						false,
						false,
						true //This damage is triggered because it occurs after a spell hit
				));
			}
		}
	}
	
}
