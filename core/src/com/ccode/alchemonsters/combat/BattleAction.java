package com.ccode.alchemonsters.combat;

public class BattleAction {

	public BattleActionType type;
	public int id;
	
	public enum BattleActionType {
		MOVE,
		SWITCH,
		USE
	}
	
}
