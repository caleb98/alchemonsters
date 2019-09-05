package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.util.GameRandom;

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
	public int delayAmount = 1;
	
	public boolean rollHit(BattleContext context, Creature source, Creature target) {
		float accuracy = this.accuracy;
		//TODO: globalize variables related to deluge accuracy increase 
		if(context.battleground.weather == WeatherType.DELUGE && elementType == ElementType.LIGHTNING) {
			accuracy += 0.2f;
		}
		//TODO: globalize variables related to pyronimbus accuracy decrease
		else if(context.battleground.weather == WeatherType.PYRONIMBUS && elementType == ElementType.WATER) {
			accuracy -= 0.2f;
		}
		//TODO: globalize variables related to tempest accuracy increase
		else if(context.battleground.weather == WeatherType.TEMPEST) {
			if(elementType == ElementType.WATER || elementType == ElementType.AIR) {
				accuracy += 0.2f;
			}
		}
		return GameRandom.nextFloat() < accuracy;
	}
	
	public static enum TurnType {
		INSTANT,
		CHARGE,
		RECHARGE,
		DELAYED
	}
	
}
