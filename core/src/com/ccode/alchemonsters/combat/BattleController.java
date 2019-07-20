package com.ccode.alchemonsters.combat;

import java.util.ArrayList;
import java.util.function.Predicate;

public interface BattleController {
	
	public void setAvailableActions(ArrayList<BattleAction> actions);
	public ArrayList<BattleAction> getAvailableActions();
	public boolean isActionSelected();
	public BattleAction getSelectedAction();
	public void filterActions(Predicate<BattleAction> filter);
	
	/**
	 * This method will be called on a battle controller whenever a 
	 * new set of actions is available and the controller needs to 
	 * select a new action. It should result in isActionSelected() being
	 * equal to false, until the controller has selected a new action.
	 */
	public void refresh();
	
}
