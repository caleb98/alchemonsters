package com.ccode.alchemonsters.combat.ailments;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.Subscriber;

public abstract class StatusAilmentEffect implements Publisher {

	public final String id;
	
	private StatusAilmentEffect() {
		id = null;
	}
	
	public StatusAilmentEffect(String effectId) {
		id = effectId;
	}
	
	public abstract void handleMessage(Message m, BattleContext context, Creature source, Creature target, String cause);
	public abstract void apply(Subscriber subscriber, BattleContext context, Creature source, Creature target, String cause);
	public abstract void remove(Subscriber subscriber, BattleContext context, Creature source, Creature target, String cause);
	
}
