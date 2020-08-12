package com.ccode.alchemonsters.creature;

public enum StatType {

	//Core Stats
	VITAE("Vitae", true),
	FOCUS("Focus", true),
	MAGIC_ATK("Magic Attack", true),
	MAGIC_DEF("Magic Defence",true),
	PHYS_ATK("Physical Attack", true),
	PHYS_DEF("Physical Defence", true),
	PENETRATION("Penetration", true),
	RESISTANCE("Resistance", true),
	SPEED("Speed", true),
	
	//Secondary Stats
	CRIT_CHANCE("Critical Strike Chance", false),
	CRIT_MULTI("Critical Strike Multiplier", false),
	DODGE_CHANCE("Dodge Chance", false),
	STAB_MULTI("S.T.A.B. Multiplier", false);
	
	public static final StatType[] primaries = new StatType[]{VITAE, FOCUS, MAGIC_ATK, MAGIC_DEF, PHYS_ATK, PHYS_DEF, PENETRATION, RESISTANCE, SPEED};
	public static final StatType[] secondaies = new StatType[]{CRIT_CHANCE, CRIT_MULTI, DODGE_CHANCE, STAB_MULTI};
	
	public final String displayName;
	public final boolean isPrimary;
	
	StatType(String name, boolean isPrimary) {
		displayName = name;
		this.isPrimary = isPrimary;
	}
	
}
