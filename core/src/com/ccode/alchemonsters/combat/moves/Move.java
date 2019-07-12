package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.creature.ElementType;

public class Move {
	
	public static final int PRIORITY_SLOWEST = -3;
	public static final int PRIORITY_SLOWER = -2;
	public static final int PRIORITY_SLOW = -1;
	public static final int PRIORITY_NORMAL = 0;
	public static final int PRIORITY_FAST = 1;
	public static final int PRIORITY_FASTER = 2;
	public static final int PRIORITY_FASTEST = 3;
	
	public static final int CRIT_STAGE_0 = 0; //never crits
	public static final int CRIT_STAGE_1 = 1;
	public static final int CRIT_STAGE_2 = 2;
	public static final int CRIT_STAGE_3 = 3;
	public static final int CRIT_STAGE_4 = 4;

	public String name;
	public String desc;
	public float accuracy;
	public int manaCost;
	public int critStage;
	public ElementType elementType;
	public MoveType moveType;
	public MoveAction[] actions;

	public int priority;
	public TurnType turnType;
	
	public static enum TurnType {
		INSTANT,
		CHARGE,
		RECHARGE,
		DELAYED
	}
	
}
