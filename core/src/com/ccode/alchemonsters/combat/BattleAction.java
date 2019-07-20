package com.ccode.alchemonsters.combat;

public class BattleAction implements Comparable<BattleAction> {

	public enum BattleActionType {
		SWITCH,
		USE,
		MOVE,
		WAIT,
	}
	
	public BattleActionType type;
	public int id;

	public BattleAction(BattleActionType type, int id) {
		this.type = type;
		this.id = id;
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
