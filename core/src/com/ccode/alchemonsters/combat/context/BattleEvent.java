package com.ccode.alchemonsters.combat.context;

@FunctionalInterface
public interface BattleEvent {
	public void runEvent(BattleContext context);
}
