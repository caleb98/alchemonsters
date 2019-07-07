package com.ccode.alchemonsters;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleController;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Subscriber;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;

public class TeamCombatDisplay extends Table implements Subscriber, BattleController {

	private Label activeName;
	private ProgressBar activeHPDisplay;
	private Label activeHPDisplayLabel;
	private ProgressBar activeMPDisplay;
	private Label activeMPDisplayLabel;
	
	private Label inactive1Name;
	private ProgressBar inactive1HP;
	private ProgressBar inactive1MP;
	private Label inactive2Name;
	private ProgressBar inactive2HP;
	private ProgressBar inactive2MP;
	private Label inactive3Name;
	private ProgressBar inactive3HP;
	private ProgressBar inactive3MP;
	
	private SelectBox<String> actionStrings;
	private ArrayList<BattleAction> actions;
	private int selectedAction = -1;
	private boolean isActionSelected = false;
	private Label actionSubmitted;
	
	private LinkedList<Message> messageQueue = new LinkedList<>();
	
	private CreatureTeam team;
	private int active;
	private int[] inactives = new int[3];
	
	private boolean isCombatActive = false;
	
	public TeamCombatDisplay(String teamName, CreatureTeam team) {
		super(UI.DEFAULT_SKIN);
		this.team = team;
		
		top();
		add(new Label(teamName, UI.DEFAULT_SKIN));
		row();
		
		//creates upper part of team ui that displays info about active mon
		Table activeTable = new Table(UI.DEFAULT_SKIN);
		activeTable.left();
		Label activeLabel = new Label("Active", UI.DEFAULT_SKIN);
		activeName = new Label("ActiveName", UI.DEFAULT_SKIN);
		//TODO: sub to combat events
		Label activeHPLabel = new Label("HP", UI.DEFAULT_SKIN);
		activeHPDisplay = new ProgressBar(0, 100, 0.01f, false, UI.DEFAULT_SKIN);
		activeHPDisplay.setColor(Color.RED);
		activeHPDisplayLabel = new Label("X/X", UI.DEFAULT_SKIN);
		Label activeMPLabel = new Label("MP", UI.DEFAULT_SKIN);
		activeMPDisplay = new ProgressBar(0, 100, 0.01f, false, UI.DEFAULT_SKIN);
		activeMPDisplay.setColor(Color.BLUE);
		activeMPDisplayLabel = new Label("X/X", UI.DEFAULT_SKIN);
		activeTable.add(activeLabel);
		activeTable.row();
		activeTable.add();
		activeTable.add(activeName);
		activeTable.row();
		activeTable.add(activeHPLabel, activeHPDisplay, activeHPDisplayLabel);
		activeTable.row();
		activeTable.add(activeMPLabel, activeMPDisplay, activeMPDisplayLabel);
		
		Table benchTable = new Table(UI.DEFAULT_SKIN);
		benchTable.left();
		inactive1Name = new Label("<empty>", UI.DEFAULT_SKIN);
		inactive1HP = new ProgressBar(0, 100, 0.01f, false, UI.DEFAULT_SKIN);
		inactive1MP = new ProgressBar(0, 100, 0.01f, false, UI.DEFAULT_SKIN);
		inactive1HP.setColor(Color.RED);
		inactive1MP.setColor(Color.BLUE);
		inactive2Name = new Label("<empty>", UI.DEFAULT_SKIN);
		inactive2HP = new ProgressBar(0, 100, 0.01f, false, UI.DEFAULT_SKIN);
		inactive2MP = new ProgressBar(0, 100, 0.01f, false, UI.DEFAULT_SKIN);
		inactive2HP.setColor(Color.RED);
		inactive2MP.setColor(Color.BLUE);
		inactive3Name = new Label("<empty>", UI.DEFAULT_SKIN);
		inactive3HP = new ProgressBar(0, 100, 0.01f, false, UI.DEFAULT_SKIN);
		inactive3MP = new ProgressBar(0, 100, 0.01f, false, UI.DEFAULT_SKIN);
		inactive3HP.setColor(Color.RED);
		inactive3MP.setColor(Color.BLUE);
		benchTable.add(new Label("Inactive 1", UI.DEFAULT_SKIN));
		benchTable.row();
		benchTable.add();
		benchTable.add(inactive1Name, inactive1HP);
		benchTable.row();
		benchTable.add();
		benchTable.add();
		benchTable.add(inactive1MP);
		benchTable.row();
		inactive2HP.setColor(Color.RED);
		inactive2MP.setColor(Color.BLUE);
		benchTable.add(new Label("Inactive 2", UI.DEFAULT_SKIN));
		benchTable.row();
		benchTable.add();
		benchTable.add(inactive2Name, inactive2HP);
		benchTable.row();
		benchTable.add();
		benchTable.add();
		benchTable.add(inactive2MP);
		benchTable.row();
		inactive3HP.setColor(Color.RED);
		inactive3MP.setColor(Color.BLUE);
		benchTable.add(new Label("Inactive 3", UI.DEFAULT_SKIN));
		benchTable.row();
		benchTable.add();
		benchTable.add(inactive3Name, inactive3HP);
		benchTable.row();
		benchTable.add();
		benchTable.add();
		benchTable.add(inactive3MP);
		benchTable.row();
		
		Table actionSelectTable = new Table(UI.DEFAULT_SKIN);
		actionSelectTable.left();
		Label selectActionLabel = new Label("Select Action:", UI.DEFAULT_SKIN);
		actionStrings = new SelectBox<>(UI.DEFAULT_SKIN);
		actionStrings.setItems("Action 1", "Action 2", "Action 3", "...and so on...");
		TextButton confirmActionButton = new TextButton("Submit Action", UI.DEFAULT_SKIN);
		confirmActionButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(isCombatActive) {
					selectedAction = actionStrings.getSelectedIndex();
					isActionSelected = true;
					actionSubmitted.setVisible(true);
				}
			}
		});
		actionSelectTable.add(selectActionLabel).left();
		actionSelectTable.row();
		actionSelectTable.add(actionStrings).left();
		actionSelectTable.row();
		actionSelectTable.add(confirmActionButton);
		
		add(activeTable).expandX().fillX();
		row();
		add(new Label("", UI.DEFAULT_SKIN));
		row();
		add(benchTable).expandX().fillX();
		row();
		add(new Label("", UI.DEFAULT_SKIN));
		row();
		add(actionSelectTable).expandX().fillX();
		
		actionSubmitted = new Label("Action submitted!", UI.DEFAULT_SKIN);
		actionSubmitted.setVisible(false);
		
		row();
		add(actionSubmitted);
		
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
				MCombatStarted full = (MCombatStarted) m;
				if(full.teamA == team || full.teamB == team) {
					initCreatureDisplays();
					isCombatActive = true;
				}
			}
			else if(m instanceof MCombatFinished) {
				MCombatFinished full = (MCombatFinished) m;
				if(full.won == team) {
					actionSubmitted.setText("WON!");
				}
				else {
					actionSubmitted.setText("LOST!");
				}
				actionSubmitted.setVisible(true);
				isCombatActive = false;
			}
			else if(m instanceof MCombatDamageDealt) {
				updateActiveDisplay();
			}
			else if(m instanceof MCombatTeamActiveChanged) {
				updateAllDisplay();
			}
		}
	}
	
	private void updateAllDisplay() {
		activeName.setText(team.active().personalName);
		activeHPDisplay.setRange(0, team.active().maxHealth);
		activeHPDisplay.setValue(team.active().currentHealth);
		activeHPDisplayLabel.setText(String.format("%2s / %2s", team.active().currentHealth, team.active().maxHealth));
		activeMPDisplay.setRange(0, team.active().maxMana);
		activeMPDisplay.setValue(team.active().currentMana);
		activeMPDisplayLabel.setText(String.format("%2s / %2s", team.active().currentMana, team.active().maxMana));
		
		int insert = 0;
		for(int i = 0; i < team.creatures.length; ++i) {
			if(i != team.active) {
				inactives[insert] = i;
				insert++;
			}
		}
		
		//Setup inactives
		if(team.creatures[inactives[0]] != null) {
			inactive1HP.setRange(0, team.creatures[inactives[0]].maxHealth);
			inactive1HP.setValue(team.creatures[inactives[0]].currentHealth);
			inactive1MP.setRange(0, team.creatures[inactives[0]].maxMana);
			inactive1MP.setValue(team.creatures[inactives[0]].currentMana);
		}
		
		if(team.creatures[inactives[1]] != null) {
			inactive2Name.setText(team.creatures[inactives[1]].personalName);
			inactive2HP.setRange(0, team.creatures[inactives[1]].maxHealth);
			inactive2HP.setValue(team.creatures[inactives[1]].currentHealth);
			inactive2MP.setRange(0, team.creatures[inactives[1]].maxMana);
			inactive2MP.setValue(team.creatures[inactives[1]].currentMana);
		}
		
		if(team.creatures[inactives[2]] != null) {
			inactive3Name.setText(team.creatures[inactives[2]].personalName);
			inactive3HP.setRange(0, team.creatures[inactives[2]].maxHealth);
			inactive3HP.setValue(team.creatures[inactives[2]].currentHealth);
			inactive3MP.setRange(0, team.creatures[inactives[2]].maxMana);
			inactive3MP.setValue(team.creatures[inactives[2]].currentMana);
		}
	}
	
	private void updateActiveDisplay() {
		activeHPDisplay.setValue(team.active().currentHealth);
		activeHPDisplayLabel.setText(String.format("%2s / %2s", team.active().currentHealth, team.active().maxHealth));
		activeMPDisplay.setValue(team.active().currentMana);
		activeMPDisplayLabel.setText(String.format("%2s / %2s", team.active().currentMana, team.active().maxMana));
	}
	
	private void initCreatureDisplays() {
		//Setup active display
		updateAllDisplay();
		actionSubmitted.setText("Action Submitted!");
		refresh();
	}
	
	@Override
	public void handleMessage(Message currentMessage) {
		messageQueue.add(currentMessage);
	}
	
	@Override
	public ArrayList<BattleAction> getAvailableActions() {
		return actions;
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
	
	@Override
	public boolean isActionSelected() {
		return isActionSelected;
	}
	
	@Override
	public void refresh() {
		isActionSelected = false;
		selectedAction = -1;
		actionSubmitted.setVisible(false);
	}
	
	@Override
	public void setAvailableActions(ArrayList<BattleAction> actions) {
		this.actions = actions;
		Array<String> stringVer = new Array<>();
		for(BattleAction a : actions) {
			switch(a.type) {
			
			case MOVE:
				stringVer.add("Use move " + team.active().moves[a.id]);
				break;
				
			case SWITCH:
				stringVer.add("Switch to " + team.creatures[a.id].personalName);
				break;
				
			case USE:
				//TODO: use inventory item
				stringVer.add("Use item [not implemented]");
				break;
			
			}
		}
		actionStrings.setItems(stringVer);
	}
	
}
