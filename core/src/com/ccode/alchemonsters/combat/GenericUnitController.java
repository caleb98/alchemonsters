package com.ccode.alchemonsters.combat;

import java.util.ArrayList;
import java.util.function.Predicate;

public class GenericUnitController implements UnitController {
	
	private ArrayList<BattleAction> actions = new ArrayList<>();
	private int selectedAction = -1; //Index of selected action in the actions list
	private boolean isActionSubmitted = false; 
	
	private int chargingMove = -1;
	private int chargingTargetPos = -1;
	
	private boolean isRecharging = false;
	
	@Override
	public void setAvailableActions(ArrayList<BattleAction> actions) {
		this.actions = actions;
	}

	@Override
	public ArrayList<BattleAction> getAvailableActions() {
		return actions;
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
			return actions.get(selectedAction);
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

	@Override
	public void filterActions(Predicate<BattleAction> filter) {
		actions.removeIf(filter);
	}
	
}
