package com.ccode.alchemonsters.combat;

import java.util.ArrayList;

public interface IBattleController {
	
	public void setAvailableActions(ArrayList<BattleAction> actions);
	public ArrayList<BattleAction> getAvailableActions();
	public boolean isActionSelected();
	public BattleAction getSelectedAction();
	
}
