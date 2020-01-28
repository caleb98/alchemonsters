package com.ccode.alchemonsters.combat.moves;

public enum MoveTargetSelectType {

	/**
	 * For moves that may select one enemy to target.
	 */
	SINGLE_OPPONENT,
	/**
	 * For moves that select the entire enemy team as a target.
	 * These moves may not have a move action that requires a MoveTarget.
	 */
	OPPONENT_TEAM,
	/**
	 * For moves that select a single friendly target.
	 */
	SINGLE_FRIENDLY,
	/**
	 * For moves that select the entire friendly team as a target.
	 * These moves may not have a move action that requires a MoveTarget.
	 */
	FRIENDLY_TEAM,
	/**
	 * For moves that always target the caster.
	 */
	SELF,
	/**
	 * For moves that have no target (they only affect the battlefield).
	 * These moves may not have a move action that requires a MoveTarget.
	 */
	NONE,
	
}
