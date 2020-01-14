package com.ccode.alchemonsters.creature;

public class CreatureNature {

	public StatType increased;
	public StatType decreased;
	
	private CreatureNature() {}
	
	public CreatureNature(StatType increase, StatType decrease) {
		if(!increase.isPrimary || !decrease.isPrimary) {
			//TODO: handle this error better
			System.err.println("ERROR: INVALID STAT TYPE PROVIDED FOR CREATURE NATURE");
		}
		
		increased = increase;
		decreased = decrease;
	}
	
}
