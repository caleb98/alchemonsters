package com.ccode.alchemonsters.combat.context;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;

/**
 * A generic damage event that is <i>not</i> caused by a move.
 */
public class BattleEventDamage implements BattleEvent, Publisher {

	private String source;
	private Creature target;
	private int amt;
	private boolean isTriggered;
	
	public BattleEventDamage(String source, Creature target, int amt, boolean isTriggered) {
		this.source = source;
		this.target = target;
		this.amt = amt;
		this.isTriggered = isTriggered;
	}
	
	@Override
	public void runEvent(BattleContext context) {
		target.modifyHealth(-amt);
		publish(new MCombatDamageDealt(
				context,
				null,
				target,
				source,
				null,
				amt,
				false,
				false,
				isTriggered
		));
	}
	
}
