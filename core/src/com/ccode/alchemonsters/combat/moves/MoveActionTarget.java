package com.ccode.alchemonsters.combat.moves;

public enum MoveActionTarget {
	
	/**
	 * Targets the caster
	 */
	SELF,
	/**
	 * Targets the selected target. 
	 * Not compatible with moves using the MoveActionSelectType
	 * OPPONENT_TEAM, FRIENDLY_TEAM, or NONE.
	 */
	TARGET,
	/**
	 * Targets all units on the caster's team
	 */
	SELF_TEAM,
	/**
	 * Targets all unit on the caster's opposing team
	 */
	OPPONENT_TEAM,
	
}
