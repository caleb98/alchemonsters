package com.ccode.alchemonsters.combat.effect;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;

public class TestEffect extends Effect {

	private Creature appliedTo;
	
	public TestEffect() {
		super("Test Effect", false);
	}

	@Override
	public void onApply(Creature c) {
		appliedTo = c;
		subscribe(MCombatDamageDealt.ID);
	}
	
	@Override
	public void onRemove(Creature c) {
		appliedTo = null;
		unsubscribe(MCombatDamageDealt.ID);
	}
	
	@Override
	public void handleMessage(Message currentMessage) {
		if(currentMessage instanceof MCombatDamageDealt) {
			MCombatDamageDealt damage = (MCombatDamageDealt) currentMessage;
			if(damage.target == appliedTo && !damage.isTriggered) {
				int d = appliedTo.getCurrentHealth();
				appliedTo.modifyHealth(-d);
				publish(new MCombatDamageDealt(
						damage.context,
						appliedTo,
						appliedTo,
						"Test Effect",
						ElementType.FIRE,
						d,
						false,
						false,
						true));
				appliedTo.removeEffect(this);
			}
		}
	}
	
}
