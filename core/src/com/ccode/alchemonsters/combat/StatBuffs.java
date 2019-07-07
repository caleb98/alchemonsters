package com.ccode.alchemonsters.combat;

public class StatBuffs {

	public byte magicPower = 0;
	public byte magicPenetration = 0;
	public byte magicResistance = 0;
	public byte physPower = 0;
	public byte physPenetration = 0;
	public byte physResistance = 0;
	public byte speed = 0;
	
	public float getMagicPowerMultiplier() {
		return 1 + (magicPower * 0.5f);
	}
	
	public float getMagicPenMultiplier() {
		return 1 + (magicPenetration * 0.5f);
	}
	
	public float getMagicResMultiplier() {
		return 1 + (magicResistance * 0.5f);
	}
	
	public float getPhysPowerMultiplier() {
		return 1 + (physPower * 0.5f);
	}
	
	public float getPhysPenMultiplier() {
		return 1 + (physPenetration * 0.5f);
	}
	
	public float getPhysResMultiplier() {
		return 1 + (physResistance * 0.5f);
	}
	
	public float getSpeedMultiplier() {
		return 1 + (speed * 0.5f);
	}
	
	public void addMagicPowerBuff(byte amt) {
		magicPower += amt;
		if(magicPower > 6) magicPower = 6;
		if(magicPower < -6) magicPower = -6;
	}
	
	public void addMagicPenBuff(byte amt) {
		magicPenetration += amt;
		if(magicPenetration > 6) magicPenetration = 6;
		if(magicPenetration < -6) magicPenetration = -6;
	}
	
	public void addMagicResBuff(byte amt) {
		magicResistance += amt;
		if(magicResistance > 6) magicResistance = 6;
		if(magicResistance < -6) magicResistance = -6;
	}
	
	public void addPhysPowerBuff(byte amt) {
		physPower += amt;
		if(physPower > 6) physPower = 6;
		if(physPower < -6) physPower= -6;
	}
	
	public void addPhysPenBuff(byte amt) {
		physPenetration += amt;
		if(physPenetration > 6) physPenetration = 6;
		if(physPenetration < -6) physPenetration = -6;
	}
	
	public void addPhysResBuff(byte amt) {
		physResistance += amt;
		if(physResistance > 6) physResistance = 6;
		if(physResistance < -6) physResistance= -6;
	}
	
	public void addSpeedBuff(byte amt) {
		speed += amt;
		if(speed > 6) speed = 6;
		if(speed < -6) speed = -6;
	}
	
	public void reset() {
		magicPower = 0;
		magicPenetration = 0;
		magicResistance = 0;
		physPower = 0;
		physPenetration = 0;
		physResistance = 0;
		speed = 0;
	}
	
}
