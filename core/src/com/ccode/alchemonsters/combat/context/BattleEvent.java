package com.ccode.alchemonsters.combat.context;

import java.util.function.IntSupplier;

public abstract class BattleEvent {
	
	private IntSupplier speedSupplier;
	
	public BattleEvent(IntSupplier speedSupplier) {
		this.speedSupplier = speedSupplier;
	}

	public int getSpeed() {
		return speedSupplier.getAsInt();
	}
	
	public abstract void runEvent(BattleContext context);

}
