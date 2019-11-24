package com.ccode.alchemonsters.net;

import java.util.ArrayList;
import java.util.function.Predicate;

import com.ccode.alchemonsters.combat.BattleAction;

public class NetFilterAllActions {

	public ArrayList<BattleAction> newActions;
	public int activePos;
	
	private NetFilterAllActions() {}
	
	public NetFilterAllActions(ArrayList<BattleAction> newActions, int activePos) {
		this.newActions = newActions;
		this.activePos = activePos;
	}
	
}
