package com.ccode.alchemonsters.combat;

import java.util.ArrayList;
import java.util.function.Predicate;

public class GenericUnitController implements UnitController {
	
	private ArrayList<BattleAction> allActions = new ArrayList<>();
	private ArrayList<BattleAction> availableActions = new ArrayList<>();
	private BattleAction selectedAction = null; //Index of selected action in the actions list
	private boolean isActionSubmitted = false; 
	
	private int chargingMove = -1;
	private int[] chargingFriendlyTargets = new int[]{};
	private int[] chargingEnemyTargets = new int[]{};
	
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
		availableActions.clear();
		availableActions.addAll(allActions);
		availableActions.removeIf(filter);
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
		return selectedAction;
	}
	
	@Override
	public void setSelectedAction(int selectedIndex) {
		selectedAction = availableActions.get(selectedIndex);
	}
	
	@Override
	public void setCharging(int move, int[] friendlyTargets, int[] enemyTargets) {
		chargingMove = move;
		chargingFriendlyTargets = friendlyTargets;
		chargingEnemyTargets = enemyTargets;
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
	public int[] getChargingFriendlyTargets() {
		return chargingFriendlyTargets;
	}
	
	@Override
	public int[] getChargingEnemyTargets() {
		return chargingEnemyTargets;
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
		selectedAction = null;
		isActionSubmitted = false;
	}
	
}
