package com.ccode.alchemonsters.combat;

public class BattleAction implements Comparable<BattleAction> {

	public enum BattleActionType {
		SWITCH,
		USE,
		WAIT,
		MOVE,
	}
	
	public final BattleActionType type;
	public final int[] targets;
	/**
	 * Represents various values depending on the BattleActionType of this action.
	 * For switch: the index of the creature to switch to in the team.
	 * For use: the index of the item to be used in the inventory.
	 * For wait: always -1
	 * For move: the index of the move to use in the attacker's move array
	 */
	public final int id;

	private BattleAction() {
		type = null;
		targets = new int[]{};
		id = -1;
	}
	
	public BattleAction(BattleActionType type, int id, int... targets) {
		this.type = type;
		this.targets = targets;
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof BattleAction)) {
			return false;
		}
		
		BattleAction other = (BattleAction) obj;
		return type == other.type && targets == other.targets && id == other.id;
	}
	
	@Override
	public int compareTo(BattleAction o) {
		return type.compareTo(o.type);
	}
	
}
