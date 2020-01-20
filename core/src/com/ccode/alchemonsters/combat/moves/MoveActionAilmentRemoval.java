package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.creature.Creature;

public class MoveActionAilmentRemoval implements MoveAction {

	public MoveActionTarget target;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target, BattleTeam opponentTeam) {
		//TODO: ailment removal!
	}

}
