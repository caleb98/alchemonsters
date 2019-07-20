package com.ccode.alchemonsters.combat;

import java.util.ArrayList;
import java.util.function.Predicate;

import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.util.Pair;

public interface BattleController {
	
	public void setAvailableActions(ArrayList<BattleAction> actions);
	public ArrayList<BattleAction> getAvailableActions();
	public boolean isActionSelected();
	public BattleAction getSelectedAction();
	public void filterActions(Predicate<BattleAction> filter);
	
	/**
	 * Sets the move that this controller is charging
	 * @param charging 
	 */
	public void setCharging(int move);
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
