package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.creature.ElementType;

public class Move {
	
	public static final int PRIORITY_SLOWEST = -3;
	public static final int PRIORITY_SLOWER = -2;
	public static final int PRIORITY_SLOW = -1;
	public static final int PRIORITY_NORMAL = 0;
	public static final int PRIORITY_FAST = 1;
	public static final int PRIORITY_FASTER = 2;
	public static final int PRIORITY_FASTEST = 3;

	public String name;
	public String desc;
	public float accuracy;
	public int power;
	public int manaCost;
	public ElementType elementType;
	public MoveType moveType;
	public IMoveAction[] actions;

	public int priority;
	public TurnType turnType;
	
	public static enum TurnType {
		INSTANT,
		CHARGE,
		RECHARGE
	}
	
}
