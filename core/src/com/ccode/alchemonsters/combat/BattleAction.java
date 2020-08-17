package com.ccode.alchemonsters.combat;

public class BattleAction implements Comparable<BattleAction> {

	public static final int[] NONE = new int[]{};
	
	public enum BattleActionType {
		SWITCH,
		USE,
		WAIT,
		MOVE,
	}
	
	public final BattleActionType type;
	public final int[] friendlyTargets;
	public final int[] enemyTargets;
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
		friendlyTargets = new int[]{};
		enemyTargets = new int[]{};
		id = -1;
	}
	
	public BattleAction(BattleActionType type, int id, int[] friendlyTargets, int[] enemyTargets) {
		this.type = type;
		this.friendlyTargets = friendlyTargets;
		this.enemyTargets = enemyTargets;
		this.id = id;
	}
	
	@Override
	public int compareTo(BattleAction o) {
		return type.compareTo(o.type);
	}
	
}
