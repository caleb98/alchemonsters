package com.ccode.alchemonsters.creature;

public interface StatCalculator<T extends Number> {

	public T calculateStat(Creature c);
	
}
