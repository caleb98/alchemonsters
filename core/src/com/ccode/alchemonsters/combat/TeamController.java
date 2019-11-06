package com.ccode.alchemonsters.combat;

import com.ccode.alchemonsters.combat.BattleAction.BattleActionType;

/**
 * A class that manages multiple {@link UnitController}s that are used for
 * a full battle team.
 * @author caleb
 */
public class TeamController {

	private TeamUnitController[] activeControllers;
	
	public TeamController(int numActives) {
		activeControllers = new TeamUnitController[numActives];
		for(int i = 0; i < activeControllers.length; ++i) {
			activeControllers[i] = new TeamUnitController();
		}
	}
	
	public UnitController[] getControls() {
		return activeControllers;
	}
	
	private class TeamUnitController extends GenericUnitController {
		
		@Override
		public void setSelectedAction(int selectedIndex) {
			super.setSelectedAction(selectedIndex);
			//After the action is selected, make sure to remove
			//any conflicting actions from other controllers on
			//this team.
			BattleAction selected = getSelectedAction();
			for(UnitController other : activeControllers) {
				other.resetAvailableActions();
				if(selected.type == BattleActionType.SWITCH) {
					if(other != this) {
						other.filterAvailableActions((a)->{
							return (a.type == BattleActionType.SWITCH && a.id == selected.id);
						});
					}
				}
			}
		}
		
	}
	
}
