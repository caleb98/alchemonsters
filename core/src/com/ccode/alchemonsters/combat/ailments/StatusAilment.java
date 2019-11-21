package com.ccode.alchemonsters.combat.ailments;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.database.StatusAilmentDatabase;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Subscriber;

public class StatusAilment implements Subscriber {

	public BattleContext context;
	public Creature source;
	public Creature target;
	public String cause;
	public StatusAilmentEffect effect;
	
	private StatusAilment() {}
	
	public StatusAilment(BattleContext context, Creature source, Creature target, String cause, String ailmentName) {
		this.context = context;
		this.source = source;
		this.target = target;		
		this.cause = cause;
		
		effect = StatusAilmentDatabase.getAilment(ailmentName);
	}
	
	@Override
	public void handleMessage(Message currentMessage) {
		effect.handleMessage(currentMessage, context, source, target, cause);
	}
	
	public void apply() {
		effect.apply(this, context, source, target, cause);
	}
	
	public void remove() {
		effect.remove(this, context, source, target, cause);
	}
	
}
