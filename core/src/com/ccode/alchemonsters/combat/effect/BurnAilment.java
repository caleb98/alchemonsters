package com.ccode.alchemonsters.combat.effect;

import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.combat.context.BattleEventDamage;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;
import com.ccode.alchemonsters.util.GameRandom;

public class BurnAilment extends Ailment {

	public BurnAilment(int duration) {
		super("Burn", false, duration);
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
			int burnDamage = (int) (appliedTo.maxHealth / 16f);
			context.addBattleEvent(new BattleEventDamage(
					null, 
					name, 
					appliedTo, 
					ElementType.FIRE, 
					burnDamage, 
					false, 
					false, 
					false
			));
		}
	}
	
	@Override
	public void handleMessage(Message currentMessage) {
		if(currentMessage instanceof MCombatDamageDealt) {
			MCombatDamageDealt full = (MCombatDamageDealt) currentMessage;
			if(full.target == appliedTo) {
				float scorchChance = 0.10f;
				if(GameRandom.nextFloat() < 0.10f) {
					//TODO: apply scorched and remove burn
				}
			}
		}
	}
	
}
