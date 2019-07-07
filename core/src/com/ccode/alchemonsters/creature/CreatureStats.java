package com.ccode.alchemonsters.creature;

public class CreatureStats {

	public byte vitae;
	public byte focus;
	public byte magicPower;
	public byte magicPenetration;
	public byte magicResistance;
	public byte physPower;
	public byte physPenetration;
	public byte physResistance;
	public byte speed;
	
	public CreatureStats(byte vitae, byte focus, 
			byte magicPower, byte magicPenetration, byte magicResistance, 
			byte physPower, byte physPenetration,  byte physResistance, byte speed) {
		this.vitae = vitae;
		this.focus = focus;
		this.magicPower = magicPower;
		this.magicPenetration = magicPenetration;
		this.magicResistance = magicResistance;
		this.physPower = physPower;
		this.physPenetration = physPenetration;
		this.physResistance = physResistance;
		this.speed = speed;
	}
	
}
