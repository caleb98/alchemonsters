package com.ccode.alchemonsters.combat;

public class StatBuffs {

	public byte magicATK = 0;
	public byte magicDEF = 0;
	public byte physATK = 0;
	public byte physDEF = 0;
	public byte speed = 0;
	
	public float getMagicATKMultiplier() {
		return magicATK * 0.5f;
	}
	
	public float getMagicDEFMultiplier() {
		return magicDEF * 0.5f;
	}
	
	public float getPhysATKMultiplier() {
		return physATK * 0.5f;
	}
	
	public float getPhysDEFMultiplier() {
		return physDEF * 0.5f;
	}
	
	public float getSpeedMultiplier() {
		return speed * 0.5f;
	}
	
	public void addMagicATKBuff(byte amt) {
		magicATK += amt;
		if(magicATK > 6) magicATK = 6;
		if(magicATK < -6) magicATK = -6;
	}
	
	public void addMagicDEFBuff(byte amt) {
		magicDEF += amt;
		if(magicDEF > 6) magicDEF = 6;
		if(magicDEF < -6) magicDEF = -6;
	}
	
	public void addPhysATKBuff(byte amt) {
		physATK += amt;
		if(physATK > 6) physATK = 6;
		if(physATK < -6) physATK= -6;
	}
	
	public void addPhysDEFBuff(byte amt) {
		physDEF += amt;
		if(physDEF > 6) physDEF = 6;
		if(physDEF < -6) physDEF= -6;
	}
	
	public void addSpeedBuff(byte amt) {
		speed += amt;
		if(speed > 6) speed = 6;
		if(speed < -6) speed = -6;
	}
	
}
