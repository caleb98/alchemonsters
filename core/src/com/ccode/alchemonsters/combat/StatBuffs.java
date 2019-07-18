package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.StatType;

public class StatBuffs {

	private static final int MAX_BUFF_AMOUNT = 6;
	private static final float REDUCTION_STRENGTH = 1.5f;
	private static final float INCREASE_STRENGTH = 0.35f;
	
	private byte magicAtk = 0;
	private byte magicDef = 0;
	private byte physAtk = 0;
	private byte physDef = 0;
	private byte penetration = 0;
	private byte resistance = 0;
	private byte speed = 0;
	
	public float getMagicAtkMultiplier() {
		if(magicAtk < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, magicAtk);
		}
		else {
			return 1 + (magicAtk * INCREASE_STRENGTH);
		}
	}
	
	public float getMagicDefMultiplier() {
		if(magicDef < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, magicDef);
		}
		else {
			return 1 + (magicDef * INCREASE_STRENGTH);
		}
	}
	
	public float getPhysAtkMultiplier() {
		if(physAtk < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, physAtk);
		}
		else {
			return 1 + (physAtk * INCREASE_STRENGTH);
		}
	}
	
	public float getPhysDefMultiplier() {
		if(physDef < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, physDef);
		}
		else {
			return 1 + (physDef * INCREASE_STRENGTH);
		}
	}
	
	public float getPenMultiplier() {
		if(penetration < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, penetration);
		}
		else {
			return 1 + (penetration * INCREASE_STRENGTH);
		}
	}
	
	public float getResMultiplier() {
		if(resistance < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, resistance);
		}
		else {
			return 1 + (resistance * INCREASE_STRENGTH);
		}
	}
	
	public float getSpeedMultiplier() {
		if(speed < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, speed);
		}
		else {
			return 1 + (speed * INCREASE_STRENGTH);
		}
	}
	
	public void addMagicAtkBuff(byte amt) {
		magicAtk += amt;
		if(magicAtk > MAX_BUFF_AMOUNT) magicAtk = MAX_BUFF_AMOUNT;
		if(magicAtk < -MAX_BUFF_AMOUNT) magicAtk = -MAX_BUFF_AMOUNT;
	}
	
	public void addMagicDefBuff(byte amt) {
		magicDef += amt;
		if(magicDef > MAX_BUFF_AMOUNT) magicDef = MAX_BUFF_AMOUNT;
		if(magicDef < -MAX_BUFF_AMOUNT) magicDef = -MAX_BUFF_AMOUNT;
	}
	
	public void addPhysAtkBuff(byte amt) {
		physAtk += amt;
		if(physAtk > MAX_BUFF_AMOUNT) physAtk = MAX_BUFF_AMOUNT;
		if(physAtk < -MAX_BUFF_AMOUNT) physAtk = -MAX_BUFF_AMOUNT;
	}
	
	public void addPhysDefBuff(byte amt) {
		physDef += amt;
		if(physDef > MAX_BUFF_AMOUNT) physDef = MAX_BUFF_AMOUNT;
		if(physDef < -MAX_BUFF_AMOUNT) physDef= -MAX_BUFF_AMOUNT;
	}
	
	public void addPenBuff(byte amt) {
		penetration += amt;
		if(penetration > MAX_BUFF_AMOUNT) penetration = MAX_BUFF_AMOUNT;
		if(penetration < -MAX_BUFF_AMOUNT) penetration = -MAX_BUFF_AMOUNT;
	}
	
	public void addResBuff(byte amt) {
		resistance += amt;
		if(resistance > MAX_BUFF_AMOUNT) resistance = MAX_BUFF_AMOUNT;
		if(resistance < -MAX_BUFF_AMOUNT) resistance= -MAX_BUFF_AMOUNT;
	}
	
	public void addSpeedBuff(byte amt) {
		speed += amt;
		if(speed > MAX_BUFF_AMOUNT) speed = MAX_BUFF_AMOUNT;
		if(speed < -MAX_BUFF_AMOUNT) speed = -MAX_BUFF_AMOUNT;
	}
	
	public void addBuff(byte amt, StatType stat) {
		switch(stat) {
		
		case FOCUS:
			//TODO: log error
			break;
			
		case MAGIC_ATK:
			addMagicAtkBuff(amt);
			break;
			
		case MAGIC_DEF:
			addMagicDefBuff(amt);
			break;
			
		case PHYS_ATK:
			addPhysAtkBuff(amt);
			break;
			
		case PHYS_DEF:
			addPhysDefBuff(amt);
			break;
			
		case PENETRATION:
			addPenBuff(amt);
			break;
			
		case RESISTANCE:
			addResBuff(amt);
			break;
			
		case SPEED:
			addSpeedBuff(amt);
			break;
			
		case VITAE:
			//TODO: log error
			break;
		
		}
	}
	
	public void reset() {
		magicAtk = 0;
		magicDef = 0;
		physAtk= 0;
		physDef = 0;
		penetration = 0;
		resistance = 0;
		speed = 0;
	}
	
}
