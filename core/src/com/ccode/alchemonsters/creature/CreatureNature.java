package com.ccode.alchemonsters.creature;

public class CreatureNature {

	public StatType increased;
	public StatType decreased;
	
	private CreatureNature() {}
	
	public CreatureNature(StatType increase, StatType decrease) {
		increased = increase;
		decreased = decrease;
	}
	
}
