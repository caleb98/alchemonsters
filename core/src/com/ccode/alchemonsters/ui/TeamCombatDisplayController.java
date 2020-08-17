package com.ccode.alchemonsters.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
	
	public void setup(BattleTeam team, BattleTeam opponentTeam, UnitController[] activeControllers, Stage ui) {
		this.activeControllers = activeControllers;
		super.setup(team, opponentTeam, ui);
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
					if(actionStrings.getSelectedIndex() >= 0) {
						activeControllers[teamId].setSelectedAction(actionStrings.getSelectedIndex());
						activeControllers[teamId].submitAction();
						TeamCombatDisplayController.this.updateStrings();
					}
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
			if(thisTeam.get(teamId) != null && thisTeam.get(teamId).isDead()) {
				boolean isSwapAvailable = false;
				
				for(int i = thisTeam.numActives; i < CreatureTeam.TEAM_SIZE; ++i) {
					if(thisTeam.get(i) != null && !thisTeam.get(i).isDead()) {
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
					String moveName = thisTeam.get(teamId).moves[a.id];
					if(activeControllers[teamId].isCharging()) {
						stringVer.add("Continue charging " + moveName);
						break;
					}
					switch(MoveDatabase.getMove(thisTeam.get(teamId).moves[a.id]).targetSelectType) {
					
					case NONE:
						stringVer.add(
								String.format(
										"Use move %s [%s mana] (move has no target)", 
										thisTeam.get(teamId).moves[a.id], 
										MoveDatabase.getMove(moveName).manaCost));
						break;
						
					case FRIENDLY_TEAM:
						stringVer.add(
								String.format(
										"Use move %s [%s mana] (friendly team)", 
										thisTeam.get(teamId).moves[a.id], 
										MoveDatabase.getMove(moveName).manaCost));
						break;
						
					case OPPONENT_TEAM:
						stringVer.add(
								String.format(
										"Use move %s [%s mana] (opponent team)", 
										thisTeam.get(teamId).moves[a.id], 
										MoveDatabase.getMove(moveName).manaCost));
						break;
						
					case SELF:
						stringVer.add(
								String.format(
										"Use move %s [%s mana] (self)", 
										thisTeam.get(teamId).moves[a.id], 
										MoveDatabase.getMove(moveName).manaCost));
						break;
						
					case SINGLE_FRIENDLY:
						stringVer.add(
								String.format(
										"Use move %s [%s mana] (friendly %s: %s)", 
										thisTeam.get(teamId).moves[a.id], 
										MoveDatabase.getMove(moveName).manaCost, 
										a.friendlyTargets[0],
										thisTeam.get(a.friendlyTargets[0]).personalName));
						break;
						
					case SINGLE_OPPONENT:
						stringVer.add(
								String.format(
										"Use move %s [%s mana] (enemy %s: %s)", 
										thisTeam.get(teamId).moves[a.id], 
										MoveDatabase.getMove(moveName).manaCost,
										a.enemyTargets[0],
										opponentTeam.get(a.enemyTargets[0]).personalName));
						break;
						
					case ALL:
						stringVer.add(
								String.format(
										"Use move %s [%s mana] (all units)", 
										thisTeam.get(teamId).moves[a.id], 
										MoveDatabase.getMove(moveName).manaCost));
						break;
						
					case SINGLE_ANY:
						if(a.friendlyTargets.length > 0) {
							stringVer.add(
									String.format(
											"Use move %s [%s mana] (friendly %s: %s)", 
											thisTeam.get(teamId).moves[a.id], 
											MoveDatabase.getMove(moveName).manaCost, 
											a.friendlyTargets[0],
											thisTeam.get(a.friendlyTargets[0]).personalName));
						}
						else {
							stringVer.add(
									String.format(
											"Use move %s [%s mana] (enemy %s: %s)", 
											thisTeam.get(teamId).moves[a.id], 
											MoveDatabase.getMove(moveName).manaCost,
											a.enemyTargets[0],
											opponentTeam.get(a.enemyTargets[0]).personalName));
						}
						break;
						
					default:
						break;				
					
					}
					break;
					
				case SWITCH:
					stringVer.add(String.format("Switch to %s (%s)", thisTeam.get(a.id).personalName, a.id));
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





























