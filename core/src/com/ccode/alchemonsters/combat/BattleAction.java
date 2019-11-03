package com.ccode.alchemonsters.combat;

public class BattleAction implements Comparable<BattleAction> {

	public enum BattleActionType {
		SWITCH,
		USE,
		MOVE,
		WAIT,
	}
	
	public final BattleActionType type;
	public final int targetPos;
	public final int id;

	public BattleAction(BattleActionType type, int targetPos, int id) {
		this.type = type;
		this.targetPos = targetPos;
		this.id = id;
		if(id == 3) {
			System.out.println("bruh moment");
		}
	}
	
	@Override
	public int compareTo(BattleAction o) {
		if((o.type != BattleActionType.MOVE && type != BattleActionType.MOVE) ||
		    o.type == BattleActionType.MOVE && type == BattleActionType.MOVE) {
			return 0;
		}
		else {
			return type.compareTo(o.type);
		}
	}
	
}
