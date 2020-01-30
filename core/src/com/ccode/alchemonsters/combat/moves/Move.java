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

	public final String name;
	public final String desc;
	public final float accuracy;
	public final int manaCost;
	public final int critStage;
	public final ElementType elementType;
	public final MoveType moveType;
	public final MoveAction[] actions;
	public final MoveTargetSelectType targetSelectType;

	public final int priority;
	public final TurnType turnType;
	public final int delayAmount;
	
	public Move() {
		name = "<NO NAME ADDED>";             
		desc = "<NO DESCRIPTION ADDED>";      
		accuracy = 1f;                         
		manaCost = 0;                            
		critStage = CRIT_STAGE_1;                
		elementType = null;              
		moveType = null;                    
		actions = null;                 
		targetSelectType = null;
		                                             
		priority = PRIORITY_NORMAL;              
		turnType = null;                    
		delayAmount = 1;                         
	}
	
}
