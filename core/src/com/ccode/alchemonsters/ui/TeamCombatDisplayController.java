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
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.TeamController;
import com.ccode.alchemonsters.combat.UnitController;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Subscriber;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;

public class TeamCombatDisplayController extends Table implements Subscriber {
	
	private LinkedList<Message> messageQueue = new LinkedList<>();
	
	private String teamName;
	private BattleTeam team;
	private Cell<InactiveDisplay>[] allDisplays;
	private Dialog sameActionError;
	
	private TeamController teamController;
	private UnitController[] activeControllers;
	
	private boolean isSetup = false;
	
	public TeamCombatDisplayController(String teamName) {
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
		
		//Set up controls
		teamController = new TeamController(numActives);
		activeControllers = teamController.getControls();
		
		//Create array of all the displays
		allDisplays = new Cell[CreatureTeam.TEAM_SIZE];
		
		//Create the displays and add them to the array
		for(int i = 0; i < team.getNumActives(); ++i) {
			allDisplays[i] = add((InactiveDisplay) new ActiveDisplay(i)).pad(10);
			row();
		}
		
		for(int i = team.getNumActives(); i < CreatureTeam.TEAM_SIZE; ++i) {
			allDisplays[i] = add(new InactiveDisplay(i)).pad(10);
			row();
		}
		
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
		}
	}
	
	public void updateStrings() {
		for(Cell<InactiveDisplay> disp : allDisplays) {
			disp.getActor().updateStrings();
		}
	}
	
	public UnitController[] getControllers() {
		return activeControllers;
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
			
			if(creature.isDead()) {
				name.setText(creature.personalName + " <Dead>");
			}
			else {
				name.setText(creature.personalName);
			}
			hpBar.setRange(0, creature.maxHealth);
			hpBar.setValue(creature.currentHealth);
			hpLabel.setText(String.format("%s/%s", creature.currentHealth, creature.maxHealth));
			manaBar.setRange(0, creature.maxMana);
			manaBar.setValue(creature.currentMana);
			manaLabel.setText(String.format("%s/%s", creature.currentMana, creature.maxMana));	
		}
		
	}
	
	private class ActiveDisplay extends InactiveDisplay {
		
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
					if(!team.get(i).isDead()) {
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





























