package com.ccode.alchemonsters.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.UnitController;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.database.MoveDatabase;

public class TeamCombatDisplayController extends TeamCombatDisplay {
	
	private UnitController[] activeControllers;
	
	public TeamCombatDisplayController(String teamName) {
		super(teamName);
	}
	
	public void setup(BattleTeam team, UnitController[] activeControllers, Stage ui) {
		this.activeControllers = activeControllers;
		super.setup(team, ui);
	}
	
	public UnitController[] getControllers() {
		return activeControllers;
	}
	
	@Override
	CreatureDisplay createActiveDisplay(int teamId) {
		return new ActiveDisplay(teamId);
	}
	
	private class ActiveDisplay extends CreatureDisplay {
		
		SelectBox<String> actionStrings;
		TextButton submitButton;
		Label submitConfirm;
		
		ActiveDisplay(int teamId) {
			super(teamId);
			
			add(new Label("Select Action: ", UI.DEFAULT_SKIN));
			actionStrings = new SelectBox<String>(UI.DEFAULT_SKIN);
			actionStrings.setItems("Action 1", "Action 2", "...");
			add(actionStrings);
			row();
			submitButton = new TextButton("Submit", UI.DEFAULT_SKIN);
			add(submitButton);
			submitConfirm = new Label("Submitted", UI.DEFAULT_SKIN);
			add(submitConfirm);
			row();
			
			submitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					activeControllers[teamId].setSelectedAction(actionStrings.getSelectedIndex());
					activeControllers[teamId].submitAction();
					TeamCombatDisplayController.this.updateStrings();
				}
			});
			
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			submitConfirm.setVisible(activeControllers[teamId].isActionSubmitted());
		}
		
		@Override
		void updateStrings() {
			//Update other relevant strings
			super.updateStrings();
			
			//See if this mon is dead and there's no other mon
			//that can be swapped in.
			if(team.get(teamId) != null && team.get(teamId).isDead()) {
				boolean isSwapAvailable = false;
				
				for(int i = team.getNumActives(); i < CreatureTeam.TEAM_SIZE; ++i) {
					if(team.get(i) != null && !team.get(i).isDead()) {
						isSwapAvailable = true;
						break;
					}
				}
				
				if(!isSwapAvailable) {
					Array<String> strings = new Array<>();
					strings.add("< Dead >");
					actionStrings.setItems(strings);
					activeControllers[teamId].submitAction();
					return;
				}
			}
	
			Array<String> stringVer = new Array<>();
			for(BattleAction a : activeControllers[teamId].getAvailableActions()) {
				switch(a.type) {
				
				case MOVE:
					String moveName = team.get(teamId).moves[a.id];
					if(activeControllers[teamId].isCharging()) {
						stringVer.add("Continue charging " + moveName);
						break;
					}
					stringVer.add(String.format("Use move %s [%s mana] (target: %s)", team.get(teamId).moves[a.id], MoveDatabase.getMove(moveName).manaCost, a.targetPos));
					break;
					
				case SWITCH:
					stringVer.add(String.format("Switch to %s (%s)", team.get(a.id).personalName, a.id));
					break;
					
				case USE:
					//TODO: use inventory item
					stringVer.add("Use item [not implemented]");
					break;
					
				case WAIT:
					if(activeControllers[teamId].isRecharging()) {
						stringVer.add("Wait (recharge)");
						break;
					}
					stringVer.add("Wait (do nothing)");
					break;
				
				}
			}
			actionStrings.setItems(stringVer);
		}
		
	}
	
}





























