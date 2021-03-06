package com.ccode.alchemonsters.combat.context;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.messages.MCombatHealingReceived;

/**
 * A generic healing event that is <i>not</i> caused by a move.
 */
public class BattleEventHealing extends BattleEvent implements Publisher {

	private String source;
	private Creature target;
	private int amt;
	private boolean isTriggered;
	
	public BattleEventHealing(String source, Creature target, int amt, boolean isTriggered) {
		//TODO: this uses a speed and priority of 0. this is fine right now because these events dont occur inline with action events, but if they ever do they will need to be sorted accordingly
		super(()->{return 0;}, ()->{return 0;});
		this.source = source;
		this.target = target;
		this.amt = amt;
		this.isTriggered = isTriggered;
	}
	
	@Override
	public void runEvent(BattleContext context) {
		target.modifyHealth(amt);
		publish(new MCombatHealingReceived(
				context, 
				null, 
				target, 
				source, 
				amt, 
				isTriggered
		));
	}

}
