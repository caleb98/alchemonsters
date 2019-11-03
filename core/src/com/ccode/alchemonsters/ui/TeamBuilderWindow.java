package com.ccode.alchemonsters.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.engine.UI;

public class TeamBuilderWindow extends Window {

	private Label teamTitle;
	private Label member1;
	private TextButton member1Edit;
	private Label member2;
	private TextButton member2Edit;
	private Label member3;
	private TextButton member3Edit;
	private Label member4;
	private TextButton member4Edit;
	
	private CreatureTeam team;
	private CreatureEditWindow editWindow;
	private int currentEditId;
	
	public TeamBuilderWindow(Stage ui, String teamName, CreatureTeam team) {
		super("Team Editor", UI.DEFAULT_SKIN);
		
		top();
		setMovable(false);
		
		teamTitle = new Label(teamName, UI.DEFAULT_SKIN);
		add(teamTitle).padTop(10).left();
		row();
		
		member1 = new CreatureNameLabel(0);
		member1Edit = new EditButton(0);
		add(member1).expandX().left();
		add(member1Edit).right().fillX();
		row();
		
		member2 = new CreatureNameLabel(1);
		member2Edit = new EditButton(1);
		add(member2).expandX().left();
		add(member2Edit).right().fillX();
		row();
		
		member3 = new CreatureNameLabel(2);
		member3Edit = new EditButton(2);
		add(member3).expandX().left();
		add(member3Edit).right().fillX();
		row();
		
		member4 = new CreatureNameLabel(3);
		member4Edit = new EditButton(3);
		add(member4).expandX().left();
		add(member4Edit).right().fillX();
		row();
		
		editWindow = new CreatureEditWindow(ui);
		editWindow.addAcceptListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(editWindow.isEditComplete()) {
					team.creatures[currentEditId] = editWindow.getEditedCreature();
				}
			}
		});
		editWindow.setVisible(false);
		editWindow.getTitleLabel().setText(teamName + " Creature Editor");
		ui.addActor(editWindow);
		
		this.team = team;
		
	}
	
	private class EditButton extends TextButton {
		
		EditButton(int id) {
			super("Edit / Add", UI.DEFAULT_SKIN);
			addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					editWindow.show(team.creatures[id]);
					currentEditId = id;
				}
			});
		}
		
	}
	
	private class CreatureNameLabel extends Label {
		
		int id;
		
		CreatureNameLabel(int id) {
			super("<empty>", UI.DEFAULT_SKIN);
			this.id = id;
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			if(team.creatures[id] == null) {
				setText("<empty>");
			}
			else if(!team.creatures[id].personalName.equals(getText().toString())) {
				setText(team.creatures[id].personalName);
			}
		}
		
	}
	
}
