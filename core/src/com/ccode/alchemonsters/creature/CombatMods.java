package com.ccode.alchemonsters.creature;

public class CombatMods {

	private static final int MAX_BUFF_AMOUNT = 6;
	private static final float BUFF_STRENGTH = 0.35f;
	
	private byte magicAtk = 0;
	private byte magicDef = 0;
	private byte physAtk = 0;
	private byte physDef = 0;
	private byte penetration = 0;
	private byte resistance = 0;
	private byte speed = 0;
	
	public float getMagicAtkMultiplier() {
		if(magicAtk < 0) {
			return (float) (1f / (1 + BUFF_STRENGTH * -magicAtk));
		}
		else {
			return 1 + (magicAtk * BUFF_STRENGTH);
		}
	}
	
	public float getMagicDefMultiplier() {
		if(magicDef < 0) {
			return (float) (1f / (1 + BUFF_STRENGTH * -magicDef));
		}
		else {
			return 1 + (magicDef * BUFF_STRENGTH);
		}
	}
	
	public float getPhysAtkMultiplier() {
		if(physAtk < 0) {
			return (float) (1f / (1 + BUFF_STRENGTH * -physAtk));
		}
		else {
			return 1 + (physAtk * BUFF_STRENGTH);
		}
	}
	
	public float getPhysDefMultiplier() {
		if(physDef < 0) {
			return (float) (1f / (1 + BUFF_STRENGTH * -physDef));
		}
		else {
			return 1 + (physDef * BUFF_STRENGTH);
		}
	}
	
	public float getPenMultiplier() {
		if(penetration < 0) {
			return (float) (1f / (1 + BUFF_STRENGTH * -penetration));
		}
		else {
			return 1 + (penetration * BUFF_STRENGTH);
		}
	}
	
	public float getResMultiplier() {
		if(resistance < 0) {
			return (float) (1f / (1 + BUFF_STRENGTH * -resistance));
		}
		else {
			return 1 + (resistance * BUFF_STRENGTH);
		}
	}
	
	public float getSpeedMultiplier() {
		if(speed < 0) {
			return (float) (1f / (1 + 0.35 * -speed));
		}
		else {
			return 1 + (speed * BUFF_STRENGTH);
		}
	}
	
	public void addMagicAtkMod(byte amt) {
		magicAtk += amt;
		if(magicAtk > MAX_BUFF_AMOUNT) magicAtk = MAX_BUFF_AMOUNT;
		if(magicAtk < -MAX_BUFF_AMOUNT) magicAtk = -MAX_BUFF_AMOUNT;
	}
	
	public void addMagicDefMod(byte amt) {
		magicDef += amt;
		if(magicDef > MAX_BUFF_AMOUNT) magicDef = MAX_BUFF_AMOUNT;
		if(magicDef < -MAX_BUFF_AMOUNT) magicDef = -MAX_BUFF_AMOUNT;
	}
	
	public void addPhysAtkMod(byte amt) {
		physAtk += amt;
		if(physAtk > MAX_BUFF_AMOUNT) physAtk = MAX_BUFF_AMOUNT;
		if(physAtk < -MAX_BUFF_AMOUNT) physAtk = -MAX_BUFF_AMOUNT;
	}
	
	public void addPhysDefMod(byte amt) {
		physDef += amt;
		if(physDef > MAX_BUFF_AMOUNT) physDef = MAX_BUFF_AMOUNT;
		if(physDef < -MAX_BUFF_AMOUNT) physDef= -MAX_BUFF_AMOUNT;
	}
	
	public void addPenMod(byte amt) {
		penetration += amt;
		if(penetration > MAX_BUFF_AMOUNT) penetration = MAX_BUFF_AMOUNT;
		if(penetration < -MAX_BUFF_AMOUNT) penetration = -MAX_BUFF_AMOUNT;
	}
	
	public void addResMod(byte amt) {
		resistance += amt;
		if(resistance > MAX_BUFF_AMOUNT) resistance = MAX_BUFF_AMOUNT;
		if(resistance < -MAX_BUFF_AMOUNT) resistance= -MAX_BUFF_AMOUNT;
	}
	
	public void addSpeedMod(byte amt) {
		speed += amt;
		if(speed > MAX_BUFF_AMOUNT) speed = MAX_BUFF_AMOUNT;
		if(speed < -MAX_BUFF_AMOUNT) speed = -MAX_BUFF_AMOUNT;
	}
	
	public void addMod(byte amt, StatType stat) {
		switch(stat) {
			
		case MAGIC_ATK:
			addMagicAtkMod(amt);
			break;
			
		case MAGIC_DEF:
			addMagicDefMod(amt);
			break;
			
		case PHYS_ATK:
			addPhysAtkMod(amt);
			break;
			
		case PHYS_DEF:
			addPhysDefMod(amt);
			break;
			
		case PENETRATION:
			addPenMod(amt);
			break;
			
		case RESISTANCE:
			addResMod(amt);
			break;
			
		case SPEED:
			addSpeedMod(amt);
			break;
			
		default:
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
