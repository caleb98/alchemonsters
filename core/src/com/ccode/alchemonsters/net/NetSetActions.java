package com.ccode.alchemonsters.net;

import java.util.ArrayList;

import com.ccode.alchemonsters.combat.BattleAction;

public class NetSetActions {

	public ArrayList<BattleAction> actions;
	public int activePos;
	
	private NetSetActions() {}
	
	public NetSetActions(ArrayList<BattleAction> actions, int activePos) {
		this.actions = actions;
		this.activePos = activePos;
	}
	
}
