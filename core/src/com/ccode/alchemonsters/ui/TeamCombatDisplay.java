package com.ccode.alchemonsters.ui;

import java.util.LinkedList;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.UnitController;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.GenericUnitController;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Subscriber;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;

public class TeamCombatDisplay extends Table implements Subscriber {
	
	private LinkedList<Message> messageQueue = new LinkedList<>();
	
	private String teamName;
	private BattleTeam team;
	private Cell<InactiveDisplay>[] allDisplays;
	private TextButton submitActionsButton;
	private Label actionsSubmittedIndicator;
	private boolean[] isActionSelected;
	private Dialog sameActionError;
	
	private boolean isSetup = false;
	
	public TeamCombatDisplay(String teamName) {
		super(UI.DEFAULT_SKIN);
		this.teamName = teamName;
		
		sameActionError = new Dialog("Error", UI.DEFAULT_SKIN) {
			@Override
			protected void result(Object object) {
				boolean close = (boolean) object;
				if(close) {
					hide();
				}
			}
		};
		sameActionError.text("Cannot select multiple mons to\n"
				           + "swap to the same mon.");
		sameActionError.button("Close", true);
		
		subscribe(MCombatStarted.ID);
		subscribe(MCombatFinished.ID);
		subscribe(MCombatDamageDealt.ID);
		subscribe(MCombatTeamActiveChanged.ID);
	}
	
	public void setup(BattleTeam team, Stage ui) {
		this.team = team;
		
		clear();
		
		top();
		add(new Label(teamName, UI.DEFAULT_SKIN)).padTop(20);
		row();
		
		int numActives = team.getNumActives();
		
		isActionSelected = new boolean[numActives];
		for(int i = 0; i < isActionSelected.length; ++i) {
			isActionSelected[i] = false;
		}
		
		allDisplays = new Cell[CreatureTeam.TEAM_SIZE];
		
		for(int i = 0; i < team.getNumActives(); ++i) {
			allDisplays[i] = add((InactiveDisplay) new ActiveDisplay(i)).pad(10);
			row();
		}
		
		for(int i = team.getNumActives(); i < CreatureTeam.TEAM_SIZE; ++i) {
			allDisplays[i] = add(new InactiveDisplay(i)).pad(10);
			row();
		}
		
		Table submitRow = new Table(UI.DEFAULT_SKIN);
		submitActionsButton = new TextButton("Submit Actions", UI.DEFAULT_SKIN);
		submitActionsButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String[] selectedActions = new String[numActives];
				int[] selectedIndexes = new int[numActives];
				ActiveDisplay[] displays = new ActiveDisplay[numActives];
				
				//Get all of the action strings that have been selected
				for(int i = 0; i < team.getNumActives(); ++i) {
					Cell<InactiveDisplay> disp = allDisplays[i];
					ActiveDisplay active = (ActiveDisplay) disp.getActor();
						 
					displays[i] = active;
					
					if(team.get(active.teamId) == null || team.get(active.teamId).isDead()) {
						//If the unit is dead we still need to
						//update their controller to have an action
						//selected, so just set it to 0 since 
						//none of the actions will be executed anyway.
						selectedActions[i] = null;
						selectedIndexes[i] = 0;
						continue;
					}
					
					int selected = active.actionStrings.getSelectedIndex();
					selectedActions[i] = active.actionStrings.getSelected();
					selectedIndexes[i] = selected;
				}
				
				//Check to make sure that we're not trying to sawp to the
				//same creature from multiple different active mons.
				for(String a : selectedActions) {
					for(String b : selectedActions) {
						if(a != null && b != null && a != b && a.equals(b)) {
							//Tried to swap to the same creature. Show
							//the error dialog.
							sameActionError.show(ui);
							return;
						}
					}
				}
				
				//No overlaps, so run the actions;
				for(int i = 0; i < team.getNumActives(); ++i) {
					displays[i].controller.setSelectedAction(selectedIndexes[i]);
					displays[i].controller.submitAction();
				}
				
			}
		});
		submitRow.add(submitActionsButton);
		
		actionsSubmittedIndicator = new Label("Submitted", UI.DEFAULT_SKIN);
		actionsSubmittedIndicator.setVisible(false);
		submitRow.add(actionsSubmittedIndicator);
		
		add(submitRow).padTop(20);
		
		isSetup = true;
	}

	@Override
	public void act(float delta) {		
		super.act(delta);
		
		if(isSetup) {
			Message m;
			while((m = messageQueue.poll()) != null) {		
				if(m instanceof MCombatStarted) {
					updateStrings();
				}
				else if(m instanceof MCombatFinished) {
					updateStrings();
				}
				else if(m instanceof MCombatDamageDealt) {
					updateStrings();
				}
				else if(m instanceof MCombatTeamActiveChanged) {
					updateStrings();
				}
			}
			
			for(boolean isSelected : isActionSelected) {
				if(!isSelected) {
					actionsSubmittedIndicator.setVisible(false);
					return;
				}
			}
			actionsSubmittedIndicator.setVisible(true);
		}
	}
	
	public void updateStrings() {
		for(Cell<InactiveDisplay> disp : allDisplays) {
			disp.getActor().updateStrings();
		}
	}
	
	public UnitController[] getControllers() {
		UnitController[] controllers = new UnitController[team.getNumActives()];
		for(int i = 0; i < team.getNumActives(); ++i) {
			controllers[i] = ((ActiveDisplay) allDisplays[i].getActor()).controller;
		}
		return controllers;
	}
	
	@Override
	public void handleMessage(Message currentMessage) {
		messageQueue.add(currentMessage);
	}
	
	private class InactiveDisplay extends Table {
		
		int teamId;
		Label name;
		ProgressBar hpBar;
		Label hpLabel;
		ProgressBar manaBar;
		Label manaLabel;
		
		InactiveDisplay(int teamId) {
			this.teamId = teamId;
			name = new Label("Mon Name", UI.DEFAULT_SKIN);
			add(name);
			row();
			
			add(new Label("HP", UI.DEFAULT_SKIN));
			hpBar = new ProgressBar(0, 10, 1, false, UI.DEFAULT_SKIN);
			add(hpBar);
			hpLabel = new Label("0/0", UI.DEFAULT_SKIN);
			add(hpLabel);
			row();
			
			add(new Label("MP", UI.DEFAULT_SKIN));
			manaBar = new ProgressBar(0, 10, 1, false, UI.DEFAULT_SKIN);
			add(manaBar);
			manaLabel = new Label("0/0", UI.DEFAULT_SKIN);
			add(manaLabel);
			row();
		}
		
		void updateStrings() {
			Creature creature = team.get(teamId);
			
			if(creature == null) {
				return;
			}
			
			name.setText(creature.personalName);
			hpBar.setRange(0, creature.maxHealth);
			hpBar.setValue(creature.currentHealth);
			hpLabel.setText(String.format("%s/%s", creature.currentHealth, creature.maxHealth));
			manaBar.setRange(0, creature.maxMana);
			manaBar.setValue(creature.currentMana);
			manaLabel.setText(String.format("%s/%s", creature.currentMana, creature.maxMana));	
		}
		
	}
	
	private class ActiveDisplay extends InactiveDisplay {
		
		GenericUnitController controller = new GenericUnitController();
		
		SelectBox<String> actionStrings;
		
		ActiveDisplay(int teamId) {
			super(teamId);
			
			add(new Label("Select Action: ", UI.DEFAULT_SKIN));
			actionStrings = new SelectBox<String>(UI.DEFAULT_SKIN);
			actionStrings.setItems("Action 1", "Action 2", "...");
			add(actionStrings);
			row();
			
			actionStrings.addListener(new EventListener() {
				@Override
				public boolean handle(Event event) {
					return false;
				}
			});
			
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			isActionSelected[teamId] = controller.isActionSubmitted();
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
					if(!team.get(i).isDead()) {
						isSwapAvailable = true;
						break;
					}
				}
				
				if(!isSwapAvailable) {
					Array<String> strings = new Array<>();
					strings.add("< Dead >");
					actionStrings.setItems(strings);
					return;
				}
			}
	
			Array<String> stringVer = new Array<>();
			for(BattleAction a : controller.getAvailableActions()) {
				switch(a.type) {
				
				case MOVE:
					String moveName = team.get(teamId).moves[a.id];
					if(controller.isCharging()) {
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
					if(controller.isRecharging()) {
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





























