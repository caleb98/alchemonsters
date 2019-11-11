package com.ccode.alchemonsters.net;

import com.ccode.alchemonsters.combat.BattleTeam;

public class NetJoinVersus {

	public String username;
	public BattleTeam team;
	public int numActives;
	
	private NetJoinVersus() {}
	
	public NetJoinVersus(String username, BattleTeam team, int numActives) {
		this.username = username;
		this.team = team;
		this.numActives = numActives;
	}
	
}
