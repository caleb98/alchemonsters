package com.ccode.alchemonsters.combat.context;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;

/**
 * A generic damage event that is <i>not</i> caused by a move.
 */
public class BattleEventDamage extends BattleEvent implements Publisher {

	private Creature source;
	private String cause;
	private Creature target;
	private ElementType element;
	private int amt;
	boolean isCrit;
	boolean isStab;
	private boolean isTriggered;
	
	public BattleEventDamage(Creature source, String cause, Creature target, ElementType element, 
			int amt, boolean isCrit, boolean isStab, boolean isTriggered) {
		super(()->{return 0;});
		this.source = source;
		this.cause = cause;
		this.target = target;
		this.element = element;
		this.amt = amt;
		this.isCrit = isCrit;
		this.isStab = isStab;
		this.isTriggered = isTriggered;
	}
	
	@Override
	public void runEvent(BattleContext context) {
		target.modifyHealth(-amt);
		publish(new MCombatDamageDealt(
				context,
				source,
				target,
				cause,
				element,
				amt,
				isCrit,
				isStab,
				isTriggered
		));
	}
	
}
