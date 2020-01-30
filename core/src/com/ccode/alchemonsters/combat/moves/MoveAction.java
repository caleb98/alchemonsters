package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.engine.event.Publisher;

public interface MoveAction extends Publisher {
	
	public void activate(MoveInstance moveInstance, BattleTeam casterTeam, BattleTeam opponentTeam);
	
}
