package com.ccode.alchemonsters.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.UI;

public class TeamCombatDisplay extends Table {
	
	String teamName;
	BattleTeam thisTeam;
	BattleTeam opponentTeam;
	Cell<CreatureDisplay>[] allDisplays;
	Dialog sameActionError;
	
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
	}
	
	@SuppressWarnings("unchecked")
	public void setup(BattleTeam team, BattleTeam opponentTeam, Stage ui) {
		this.thisTeam = team;
		this.opponentTeam = opponentTeam;
		
		clear();
		
		top();
		add(new Label(teamName, UI.DEFAULT_SKIN)).padTop(20);
		row();
		
		//Create array of all the displays
		allDisplays = new Cell[CreatureTeam.TEAM_SIZE];
		
		//Create the displays and add them to the array
		for(int i = 0; i < team.numActives; ++i) {
			allDisplays[i] = add(createActiveDisplay(i)).pad(10);
			row();
		}
		
		for(int i = team.numActives; i < CreatureTeam.TEAM_SIZE; ++i) {
			allDisplays[i] = add(createInactiveDisplay(i)).pad(10);
			row();
		}
		
		updateStrings();
	}
	
	public void setTeam(BattleTeam t) {
		thisTeam = t;
	}
	
	public void updateStrings() {
		for(Cell<CreatureDisplay> disp : allDisplays) {
			disp.getActor().updateStrings();
		}
	}
	
	CreatureDisplay createInactiveDisplay(int teamId) {
		return new CreatureDisplay(teamId);
	}
	
	CreatureDisplay createActiveDisplay(int teamId) {
		CreatureDisplay disp = new CreatureDisplay(teamId);
		disp.add(new Label("Active", UI.DEFAULT_SKIN));
		return disp;
	}
	
	class CreatureDisplay extends Table {
		
		int teamId;
		Label name;
		ProgressBar hpBar;
		Label hpLabel;
		ProgressBar manaBar;
		Label manaLabel;
		
		CreatureDisplay(int teamId) {
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
			manaBar = new ProgressBar(0, 10, 1, false, UI.DEFAULT_SKIN.get("horizontal-blue", ProgressBar.ProgressBarStyle.class));
			add(manaBar);
			manaLabel = new Label("0/0", UI.DEFAULT_SKIN);
			add(manaLabel);
			row();
		}
		
		void updateStrings() {
			Creature creature = thisTeam.get(teamId);
			
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
			hpBar.setValue(creature.getCurrentHealth());
			hpLabel.setText(String.format("%s/%s", creature.getCurrentHealth(), creature.maxHealth));
			manaBar.setRange(0, creature.maxMana);
			manaBar.setValue(creature.getCurrentMana());
			manaLabel.setText(String.format("%s/%s", creature.getCurrentMana(), creature.maxMana));	
		}
		
	}
	
}





























