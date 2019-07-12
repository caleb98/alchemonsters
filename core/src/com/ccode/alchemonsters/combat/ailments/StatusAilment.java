package com.ccode.alchemonsters.combat.ailments;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.ListSubscriber;
import com.ccode.alchemonsters.engine.event.Message;

public class StatusAilment extends ListSubscriber {

	public BattleContext context;
	public Creature source;
	public Creature target;
	public String cause;
	public StatusAilmentEffect effect;
	
	public StatusAilment(BattleContext context, Creature source, Creature target, String cause, String ailmentName) {
		this.context = context;
		this.source = source;
		this.target = target;		
		this.cause = cause;
		
		effect = StatusAilmentDictionary.getAilment(ailmentName);
	}
	
	public void update() {
		Message m;
		while((m = messageQueue.poll()) != null) {
			effect.handleMessage(m, context, source, target, cause);
		}
	}
	
	public void apply() {
		effect.apply(this, context, source, target, cause);
	}
	
	public void remove() {
		effect.remove(this, context, source, target, cause);
	}
	
}
