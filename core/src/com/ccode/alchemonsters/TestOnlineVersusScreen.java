package com.ccode.alchemonsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.engine.GameScreen;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.event.ListSubscriber;
import com.ccode.alchemonsters.ui.CombatLog;
import com.ccode.alchemonsters.ui.TeamBuilderWindow;
import com.ccode.alchemonsters.ui.TeamCombatDisplay;
import com.ccode.alchemonsters.ui.TeamCombatDisplayController;

public class TestOnlineVersusScreen extends GameScreen implements InputProcessor {

	//Message listener
	private ListSubscriber sub;
	
	//Overall frame
	private TeamBuilderWindow teamBuilder;
	
	//Combat windown UI
	private Window combatWindow;
	private TeamCombatDisplayController myTeamDisplay;
	private TeamCombatDisplay theirTeamDisplay;
	private CombatLog combatTextDisplay;
	private Dialog errorMessage;
	
	//Scene2d ui
	private Stage ui;
	private Table table;
	
	//Team
	private CreatureTeam myTeam;
	
	//Battle state stuff
	private BattleContext battleContext;
	
	public TestOnlineVersusScreen(AlchemonstersGame game) {
		super(game);
	}

	@Override
	public void show() {
		myTeam = new CreatureTeam();
		
		ui = new Stage(game.uiView, game.batch);
		table = new Table(UI.DEFAULT_SKIN);
		table.setFillParent(true);
		ui.addActor(table);
		table.bottom();
		
		Table leftPane = new Table();
		teamBuilder = new TeamBuilderWindow(ui, "Team", myTeam);
		leftPane.add(teamBuilder).expandY().fill().prefWidth(300);
		leftPane.row();
		
		Window menuWindow = new Window("Options", UI.DEFAULT_SKIN);
		menuWindow.center();
		
		//TODO: start combat and create connection
		TextButton start1v1 = new TextButton("Start 1v1", UI.DEFAULT_SKIN);
		TextButton start2v2 = new TextButton("Start 2v2", UI.DEFAULT_SKIN);
		menuWindow.add(start1v1).expandX().fillX();
		menuWindow.add(start2v2).expandX().fillX();
		menuWindow.row();
		
		TextButton mainMenuButton = new TextButton("Exit to Main Menu", UI.DEFAULT_SKIN);
		mainMenuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//TODO: exit combat and close connection
			}
		});
		menuWindow.add(mainMenuButton);
		menuWindow.row();
		
		leftPane.add(menuWindow).fill();
		
		table.add(leftPane).expandY().fill().align(Align.topLeft).prefWidth(300);
		
		//Combat window setup
		combatWindow = new Window("Combat Display", UI.DEFAULT_SKIN);
		combatWindow.setMovable(false);
		
		myTeamDisplay = new TeamCombatDisplayController("My Team");
		theirTeamDisplay = new TeamCombatDisplay("Their Team");
		
		combatTextDisplay = new CombatLog();
		
		Table teamDisplayTable = new Table();
		teamDisplayTable.add(myTeamDisplay).expand().fill().left();
		teamDisplayTable.add(theirTeamDisplay).expand().fill().right();
		combatWindow.add(teamDisplayTable);
		combatWindow.row();
		combatWindow.add(combatTextDisplay).expand().fill().padTop(20);
		
		table.add(combatWindow).expand().fill();
		
		//Setup the error message
		errorMessage = new Dialog("Error", UI.DEFAULT_SKIN) {
			protected void result(Object object) {
				boolean close = (boolean) object;
				if(close) {
					hide();
				}
			}
		};
		errorMessage.text("Unable to start combat with empty team.\nPlease add at least one mon to your team.");
		errorMessage.button("Close", true);
		
		InputMultiplexer multi = new InputMultiplexer(ui, this);
		Gdx.input.setInputProcessor(multi);
		
		sub = new ListSubscriber();
		
	}
	
	@Override
	public void renderGraphics(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderUI(float delta) {
		ui.act(delta);
		ui.draw();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}	

}




















