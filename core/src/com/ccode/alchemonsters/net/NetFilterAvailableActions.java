package com.ccode.alchemonsters.net;

import java.util.function.Predicate;

import com.ccode.alchemonsters.combat.BattleAction;

public class NetFilterAvailableActions {

	public Predicate<BattleAction> filter;
	public int activePos;
	
	private NetFilterAvailableActions() {}
	
	public NetFilterAvailableActions(Predicate<BattleAction> filter, int activePos) {
		this.filter = filter;
		this.activePos = activePos;
	}
	
}
