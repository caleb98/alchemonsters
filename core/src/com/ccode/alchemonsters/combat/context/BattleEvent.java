package com.ccode.alchemonsters.combat.context;

import java.util.function.IntSupplier;

public abstract class BattleEvent {
	
	private IntSupplier speedSupplier;
	private IntSupplier prioritySupplier;
	
	public BattleEvent(IntSupplier speedSupplier, IntSupplier prioritySupplier) {
		this.speedSupplier = speedSupplier;
		this.prioritySupplier = prioritySupplier;
	}

	public int getSpeed() {
		return speedSupplier.getAsInt();
	}
	
	public int getPriority() {
		return prioritySupplier.getAsInt();
	}
	
	public abstract void runEvent(BattleContext context);

}
