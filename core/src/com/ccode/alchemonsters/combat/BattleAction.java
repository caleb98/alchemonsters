package com.ccode.alchemonsters.combat;

public class BattleAction implements Comparable<BattleAction> {

	public enum BattleActionType {
		SWITCH,
		USE,
		WAIT,
		MOVE,
	}
	
	public final BattleActionType type;
	public final int targetPos;
	public final int id;

	private BattleAction() {
		type = null;
		targetPos = -1;
		id = -1;
	}
	
	public BattleAction(BattleActionType type, int targetPos, int id) {
		this.type = type;
		this.targetPos = targetPos;
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof BattleAction)) {
			return false;
		}
		
		BattleAction other = (BattleAction) obj;
		return type == other.type && targetPos == other.targetPos && id == other.id;
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
