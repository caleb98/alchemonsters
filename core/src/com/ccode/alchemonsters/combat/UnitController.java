package com.ccode.alchemonsters.combat;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * A controller interface for managing and directing the actions
 * of a single creature unit in battle.
 * @author caleb
 *
 */
public interface UnitController {
	
	//Action Modification Functions
	/**
	 * Sets all possible actions available to this unit.
	 * This list ignores constraints from the context of
	 * the battle/combat.
	 * Calls to this method should result in any filters
	 * previously placed on this unit's actions to be removed.
	 * @param actions
	 */
	public void setAllActions(ArrayList<BattleAction> actions);
	/**
	 * Gets all the possible actions available to this unit.
	 * The returned list will ignore all constraints from the
	 * context of the battle.
	 * @return
	 */
	public ArrayList<BattleAction> getAllActions();
	/*
	 * Filters the list of all actions available to this unit.
	 * Calling this method should also cause all filters applied
	 * to the filtered action list to be removed, and the filtered
	 * action list should be updated to reflect the new all filtered
	 * list.
	 */
	public void filterAllActions(Predicate<BattleAction> filter);
	/**
	 * Filters the actions available to this unit based on a given
	 * predicate. Implementations of this function should result in
	 * the same return value for {@link #getAllActions()} but a 
	 * filtered return value for {@link #getAvailableActions()}.
	 * @param filter
	 */
	public void filterAvailableActions(Predicate<BattleAction> filter);
	/**
	 * Gets all currently available actions for this unit. This
	 * list will not include those that have been filtered out.
	 * @return
	 */
	public ArrayList<BattleAction> getAvailableActions();
	/**
	 * Removes all filters imposed on the list of available
	 * actions.
	 */
	public void resetAvailableActions();
	
	//Action Selection Functions 
	public boolean isActionSubmitted();
	public void submitAction();
	public BattleAction getSelectedAction();
	public void setSelectedAction(int selectedIndex);
	
	// ************************************* 
	//          CHARGING FUNCTIONS
	// *************************************
	
	/**
	 * Sets the move that this controller is charging
	 * @param move the index of the charging move in the mon's move list
	 * @param tar the index of the target on the opposing team
	 */
	public void setCharging(int move, int[] targets, boolean doesChargeTargetEnemy);
	public void stopCharging();
	/**
	 * Checks to see if a controller is charging any move
	 * @return 
	 */
	public boolean isCharging();
	/**
	 * @return the index of the move the active creature is charging
	 */
	public int getCharging();
	/**
	 * @return an array of indexes the charging move will target
	 */
	public int[] getChargingTargetPos();
	/**
	 * @return whether or not the currently charging move is targeting an enemy or not
	 */
	public boolean isChargeTargetingEnemy();
	
	// ************************************* 
	//         RECHARGING FUNCTIONS
	// *************************************
	
	public void setRecharging(boolean isRecharging);
	public boolean isRecharging();
	
	/**
	 * This method will be called on a battle controller whenever a 
	 * new set of actions is available and the controller needs to 
	 * select a new action. It should result in isActionSelected() being
	 * equal to false, until the controller has selected a new action.
	 */
	public void refresh();
	
}
