package com.ccode.alchemonsters.net;

import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleAction.BattleActionType;
import com.ccode.alchemonsters.combat.UnitController;
import com.esotericsoftware.kryonet.Connection;

public class ClientTeamController {

	private TeamUnitController[] activeControllers;
	
	public ClientTeamController(Connection conn, int numActives) {
		activeControllers = new TeamUnitController[numActives];
		for(int i = 0; i < activeControllers.length; ++i) {
			activeControllers[i] = new TeamUnitController(conn, i);
		}
	}
	
	public ClientUnitController[] getControls() {
		return activeControllers;
	}
	
	private class TeamUnitController extends ClientUnitController {
		
		public TeamUnitController(Connection conn, int activePos) {
			super(conn, activePos);
		}

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
