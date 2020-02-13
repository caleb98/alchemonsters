package com.ccode.alchemonsters.net;

import com.ccode.alchemonsters.combat.context.BattleContext;

public class NetBattleContextUpdate {

	public BattleContext context;
	
	private NetBattleContextUpdate() {}
	
	public NetBattleContextUpdate(BattleContext context) {
		this.context = context;
	}
	
}
