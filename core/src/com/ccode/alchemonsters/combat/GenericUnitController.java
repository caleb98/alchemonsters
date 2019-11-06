package com.ccode.alchemonsters.combat;

import java.util.ArrayList;
import java.util.function.Predicate;

public class GenericUnitController implements UnitController {
	
	private ArrayList<BattleAction> allActions = new ArrayList<>();
	private ArrayList<BattleAction> availableActions = new ArrayList<>();
	private int selectedAction = -1; //Index of selected action in the actions list
	private boolean isActionSubmitted = false; 
	
	private int chargingMove = -1;
	private int chargingTargetPos = -1;
	
	private boolean isRecharging = false;
	
	@Override
	public void setAllActions(ArrayList<BattleAction> actions) {
		allActions.clear();
		availableActions.clear();
		
		allActions.addAll(actions);
		availableActions.addAll(actions);
	}

	@Override
	public ArrayList<BattleAction> getAllActions() {
		return allActions;
	}
	
	@Override
	public void filterAllActions(Predicate<BattleAction> filter) {
		allActions.removeIf(filter);
		resetAvailableActions();
	}

	@Override
	public void filterAvailableActions(Predicate<BattleAction> filter) {
		BattleAction current = isActionSubmitted ? getSelectedAction() : null;
		availableActions.removeIf(filter);
		if(!availableActions.contains(current)) {
			refresh();
		}
	}

	@Override
	public ArrayList<BattleAction> getAvailableActions() {
		return availableActions;
	}
	
	@Override
	public void resetAvailableActions() {
		availableActions.clear();
		availableActions.addAll(allActions);
	}
	
	@Override
	public boolean isActionSubmitted() {
		return isActionSubmitted;
	}
	
	@Override
	public void submitAction() {
		isActionSubmitted = true;
	}

	@Override
	public BattleAction getSelectedAction() {
		if(selectedAction == -1) {
			return null;
		}
		else {
			return availableActions.get(selectedAction);
		}
	}
	
	@Override
	public void setSelectedAction(int selectedIndex) {
		selectedAction = selectedIndex;
	}

	@Override
	public void setCharging(int move, int tar) {
		chargingMove = move;
		chargingTargetPos = tar;
	}

	@Override
	public void stopCharging() {
		chargingMove = -1;
	}

	@Override
	public boolean isCharging() {
		return chargingMove != -1;
	}

	@Override
	public int getCharging() {
		return chargingMove;
	}
	
	@Override
	public int getChargingTargetPos() {
		return chargingTargetPos;
	}

	@Override
	public void setRecharging(boolean isRecharging) {
		this.isRecharging = isRecharging;
	}

	@Override
	public boolean isRecharging() {
		return isRecharging;
	}

	@Override
	public void refresh() {
		selectedAction = -1;
		isActionSubmitted = false;
	}
	
}
