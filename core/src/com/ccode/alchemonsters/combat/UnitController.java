package com.ccode.alchemonsters.combat;

import java.util.ArrayList;
import java.util.function.Predicate;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.Pair;

/**
 * A controller interface for managing and directing the actions
 * of a single creature unit in battle.
 * @author caleb
 *
 */
public interface UnitController {
	
	//Action Modification Functions
	public void setAvailableActions(ArrayList<BattleAction> actions);
	public ArrayList<BattleAction> getAvailableActions();
	public void filterActions(Predicate<BattleAction> filter);
	
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
	 * @param charging 
	 */
	public void setCharging(int move, int tar);
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
	public int getChargingTargetPos();
	
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
