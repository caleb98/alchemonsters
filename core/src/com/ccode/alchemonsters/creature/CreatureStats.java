package com.ccode.alchemonsters.creature;

public class CreatureStats {

	/**
	 * Determines maximum HP
	 */
	public int vitae; 
	/**
	 * Determines maximum MP
	 */
	public int focus;
	/**
	 * Determines damage for magic attacks
	 */
	public int magicAtk;
	/**
	 * Determines mitigation for magic attacks
	 */
	public int magicDef;
	/**
	 * Determines damage for physical attacks
	 */
	public int physAtk;
	/**
	 * Determines mitigation for physical attacks
	 */
	public int physDef;
	
	public int penetration;
	public int resistance;
	public int speed;
	
	private CreatureStats() {}
	
	public CreatureStats(int vitae, int focus, 
					     int magicAtk, int magicDef,
					     int physAtk, int physDef, 
					     int penetration, int resistance, 
					     int speed) {
		this.vitae = vitae;
		this.focus = focus;
		this.magicAtk = magicAtk;
		this.magicDef = magicDef;
		this.physAtk = physAtk;
		this.physDef = physDef;
		this.speed = speed;
	}
	
}
