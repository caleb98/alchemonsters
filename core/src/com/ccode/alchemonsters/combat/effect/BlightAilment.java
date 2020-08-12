package com.ccode.alchemonsters.combat.effect;

import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.engine.event.messages.MConsoleMessage;
import com.ccode.alchemonsters.util.GameRandom;

public class BlightAilment extends Ailment {

	public BlightAilment(int duration) {
		super("Blight", false, duration);
		//TODO: obscure mana bar
	}

	@Override
	public void enterState(BattleContext context, CombatState state) {
		super.enterState(context, state);
		
		if(state == CombatState.END_PHASE) {
			//TODO: should  this be a battle event?
			float drainPercent = 0.04f + GameRandom.nextFloat() * 0.08f;
			int currentMana = appliedTo.maxMana;
			int drainAmt = (int) (currentMana * drainPercent);
			appliedTo.modifyMana(-drainAmt);
			publish(new MConsoleMessage(
					String.format("Blight drained %s mana from %s!", 
							drainAmt,
							appliedTo.personalName)
			));
		}
	}
	
}
