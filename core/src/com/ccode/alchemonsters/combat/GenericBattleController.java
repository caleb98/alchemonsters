package com.ccode.alchemonsters.combat;

import java.util.ArrayList;
import java.util.function.Predicate;

public class GenericBattleController implements BattleController {
	
	private ArrayList<BattleAction> actions = new ArrayList<>();
	private int selectedAction = -1;
	
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
	public boolean isActionSelected() {
		return selectedAction != -1;
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
	
	public void setSelectedAction(int action) {
		selectedAction = action;
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
	}

	@Override
	public void filterActions(Predicate<BattleAction> filter) {
		actions.removeIf(filter);
	}
	
}
