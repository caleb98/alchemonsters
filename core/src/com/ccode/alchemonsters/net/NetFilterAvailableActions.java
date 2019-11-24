package com.ccode.alchemonsters.net;

import java.util.ArrayList;

import com.ccode.alchemonsters.combat.BattleAction;

public class NetFilterAvailableActions {

	public ArrayList<BattleAction> newActions;
	public int activePos;
	
	private NetFilterAvailableActions() {}
	
	public NetFilterAvailableActions(ArrayList<BattleAction> newActions, int activePos) {
		this.newActions = newActions;
		this.activePos = activePos;
	}
	
}
