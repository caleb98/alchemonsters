package com.ccode.alchemonsters.ui;

import java.util.LinkedList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleController;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.GenericBattleController;
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
	
	private BattleTeam team;
	private Cell<InactiveDisplay>[] allDisplays;
	private TextButton submitActionsButton;
	
	public TeamCombatDisplay(String teamName, BattleTeam team) {
		super(UI.DEFAULT_SKIN);
		this.team = team;
		
		top();
		add(new Label(teamName, UI.DEFAULT_SKIN)).padTop(20);
		row();
		
		int numActives = team.getNumActives();
		int numInactives = 4 - numActives;
		
		allDisplays = new Cell[numActives + numInactives];
		
		int activeCounter = 0;
		for(int i = 0; i < team.creatures().length; ++i) {
			if(team.isActive(i)) {
				allDisplays[i] = add((InactiveDisplay) new ActiveDisplay(i, activeCounter)).pad(10);
			}
			else {
				allDisplays[i] = add(new InactiveDisplay(i)).pad(10);
			}
			row();
		}
		
		submitActionsButton = new TextButton("Submit Actions", UI.DEFAULT_SKIN);
		submitActionsButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				for(Cell<InactiveDisplay> disp : allDisplays) {
					if(disp.getActor() instanceof ActiveDisplay) {
						ActiveDisplay active = (ActiveDisplay) disp.getActor();
						int selected = active.actionStrings.getSelectedIndex();
						active.controller.setSelectedAction(selected);
					}
				}
			}
		});
		add(submitActionsButton).padTop(20);
		
		subscribe(MCombatStarted.ID);
		subscribe(MCombatFinished.ID);
		subscribe(MCombatDamageDealt.ID);
		subscribe(MCombatTeamActiveChanged.ID);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
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
				MCombatTeamActiveChanged full = (MCombatTeamActiveChanged) m;
				if(full.team == team) {
					Cell oldActive = allDisplays[full.prevActive];
					Cell newActive = allDisplays[full.nextActive];
					
					InactiveDisplay newInactiveDisp = new InactiveDisplay(full.prevActive);
					ActiveDisplay newActiveDisp = new ActiveDisplay(full.nextActive, team.getIdPosition(full.nextActive));
					
					oldActive.setActor(newInactiveDisp);
					newActive.setActor(newActiveDisp);
					
					updateStrings();
				}
			}
		}
	}
	
	public void updateActives() {
		for(int i = 0; i < allDisplays.length; ++i) {
			InactiveDisplay old = allDisplays[i].getActor();
			//Currently an active display, but not active mon
			if((old instanceof ActiveDisplay && !team.isActive(i))) {
				InactiveDisplay newInactive = new InactiveDisplay(i);
				allDisplays[i].setActor(newInactive);
			}
			//Currently inactive display, but is an active mon
			else if(!(old instanceof ActiveDisplay) && team.isActive(i)) {
				ActiveDisplay newActive = new ActiveDisplay(i, team.getIdPosition(i));
				allDisplays[i].setActor(newActive);
			}
		}
		updateStrings();
	}
	
	public void updateStrings() {
		for(Cell<InactiveDisplay> disp : allDisplays) {
			disp.getActor().updateStrings();
		}
	}
	
	public BattleController[] getControllers() {
		BattleController[] controllers = new BattleController[team.getNumActives()];
		int controlNum = 0;
		for(Cell<InactiveDisplay> disp : allDisplays) {
			if(disp.getActor() instanceof ActiveDisplay) {
				ActiveDisplay active = (ActiveDisplay) disp.getActor();
				controllers[controlNum] = active.controller;
				controlNum++;
			}
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
		
		GenericBattleController controller = new GenericBattleController();
		int activeId;
		
		SelectBox<String> actionStrings;
		
		ActiveDisplay(int teamId, int activeId) {
			super(teamId);
			this.activeId = activeId;
			
			add(new Label("Select Action: ", UI.DEFAULT_SKIN));
			actionStrings = new SelectBox<String>(UI.DEFAULT_SKIN);
			actionStrings.setItems("Action 1", "Action 2", "...");
			add(actionStrings);
			row();
			
		}
		
		@Override
		void updateStrings() {
			//Update other relevant strings
			super.updateStrings();
	
			Array<String> stringVer = new Array<>();
			for(BattleAction a : controller.getAvailableActions()) {
				switch(a.type) {
				
				case MOVE:
					String moveName = team.active(activeId).moves[a.id];
					if(controller.isCharging()) {
						stringVer.add("Continue charging " + moveName);
						break;
					}
					stringVer.add(String.format("Use move %s [%s mana] (target: %s)", team.active(activeId).moves[a.id], MoveDatabase.getMove(moveName).manaCost, a.targetPos));
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





























