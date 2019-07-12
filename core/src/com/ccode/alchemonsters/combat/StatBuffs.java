package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.StatType;

public class StatBuffs {

	private static final int MAX_BUFF_AMOUNT = 6;
	private static final float REDUCTION_STRENGTH = 1.5f;
	private static final float INCREASE_STRENGTH = 0.35f;
	
	private byte magicPower = 0;
	private byte magicPenetration = 0;
	private byte magicResistance = 0;
	private byte physPower = 0;
	private byte physPenetration = 0;
	private byte physResistance = 0;
	private byte speed = 0;
	
	public float getMagicPowerMultiplier() {
		if(magicPower < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, magicPower);
		}
		else {
			return 1 + (magicPower * INCREASE_STRENGTH);
		}
	}
	
	public float getMagicPenMultiplier() {
		if(magicPenetration < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, magicPenetration);
		}
		else {
			return 1 + (magicPenetration * INCREASE_STRENGTH);
		}
	}
	
	public float getMagicResMultiplier() {
		if(magicResistance < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, magicResistance);
		}
		else {
			return 1 + (magicResistance * INCREASE_STRENGTH);
		}
	}
	
	public float getPhysPowerMultiplier() {
		if(physPower < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, physPower);
		}
		else {
			return 1 + (physPower * INCREASE_STRENGTH);
		}
	}
	
	public float getPhysPenMultiplier() {
		if(physPenetration < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, physPenetration);
		}
		else {
			return 1 + (physPenetration * INCREASE_STRENGTH);
		}
	}
	
	public float getPhysResMultiplier() {
		if(physResistance < 0) {
			return (float) Math.pow(REDUCTION_STRENGTH, physResistance);
		}
		else {
			return 1 + (physResistance * INCREASE_STRENGTH);
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
	
	public void addMagicPowerBuff(byte amt) {
		magicPower += amt;
		if(magicPower > MAX_BUFF_AMOUNT) magicPower = MAX_BUFF_AMOUNT;
		if(magicPower < -MAX_BUFF_AMOUNT) magicPower = -MAX_BUFF_AMOUNT;
	}
	
	public void addMagicPenBuff(byte amt) {
		magicPenetration += amt;
		if(magicPenetration > MAX_BUFF_AMOUNT) magicPenetration = MAX_BUFF_AMOUNT;
		if(magicPenetration < -MAX_BUFF_AMOUNT) magicPenetration = -MAX_BUFF_AMOUNT;
	}
	
	public void addMagicResBuff(byte amt) {
		magicResistance += amt;
		if(magicResistance > MAX_BUFF_AMOUNT) magicResistance = MAX_BUFF_AMOUNT;
		if(magicResistance < -MAX_BUFF_AMOUNT) magicResistance = -MAX_BUFF_AMOUNT;
	}
	
	public void addPhysPowerBuff(byte amt) {
		physPower += amt;
		if(physPower > MAX_BUFF_AMOUNT) physPower = MAX_BUFF_AMOUNT;
		if(physPower < -MAX_BUFF_AMOUNT) physPower= -MAX_BUFF_AMOUNT;
	}
	
	public void addPhysPenBuff(byte amt) {
		physPenetration += amt;
		if(physPenetration > MAX_BUFF_AMOUNT) physPenetration = MAX_BUFF_AMOUNT;
		if(physPenetration < -MAX_BUFF_AMOUNT) physPenetration = -MAX_BUFF_AMOUNT;
	}
	
	public void addPhysResBuff(byte amt) {
		physResistance += amt;
		if(physResistance > MAX_BUFF_AMOUNT) physResistance = MAX_BUFF_AMOUNT;
		if(physResistance < -MAX_BUFF_AMOUNT) physResistance= -MAX_BUFF_AMOUNT;
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
			
		case MAGIC_PENETRATION:
			addMagicPenBuff(amt);
			break;
			
		case MAGIC_POWER:
			addMagicPowerBuff(amt);
			break;
			
		case MAGIC_RESISTANCE:
			addMagicResBuff(amt);
			break;
			
		case PHYS_PENETRATION:
			addPhysPenBuff(amt);
			break;
			
		case PHYS_POWER:
			addPhysPowerBuff(amt);
			break;
			
		case PHYS_RESISTANCE:
			addPhysResBuff(amt);
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
		magicPower = 0;
		magicPenetration = 0;
		magicResistance = 0;
		physPower = 0;
		physPenetration = 0;
		physResistance = 0;
		speed = 0;
	}
	
}
