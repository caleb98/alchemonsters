package com.ccode.alchemonsters.combat;

@FunctionalInterface
public interface BattleEvent {
	/**
	 * 
	 * @param context
	 * @return whether or not this event is completed (true will remove this event from future triggers; false will let it remain)
	 */
	public boolean trigger(BattleContext context);
}
