package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionChance implements MoveAction {

	/**
	 * The action to run if the roll succeeds.
	 */
	public MoveAction action;
	/**
	 * The chance that the embedded action should be run.
	 */
	public float chance;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target, CreatureTeam targetTeam) {
		if(GameRandom.nextFloat() < chance) {
			action.activate(move, context, source, sourceTeam, target, targetTeam);
		}
	}
	
}