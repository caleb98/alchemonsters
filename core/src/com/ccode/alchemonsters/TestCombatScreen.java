package com.ccode.alchemonsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.BiomeType;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.TeamController;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.context.BattleContext;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.GameScreen;
import com.ccode.alchemonsters.engine.ScriptManager;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.database.CreatureDatabase;
import com.ccode.alchemonsters.engine.database.GameData;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.database.ScriptDatabase;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Subscriber;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatStateChanged;
import com.ccode.alchemonsters.ui.CombatLog;
import com.ccode.alchemonsters.ui.TeamBuilderWindow;
import com.ccode.alchemonsters.ui.TeamCombatDisplayController;

public class TestCombatScreen extends GameScreen implements InputProcessor, Screen, Subscriber {
	
	//Overall Frame
	private TeamBuilderWindow teamABuilder;
	private TeamBuilderWindow teamBBuilder;
	
	//Combat window UI
	private Window combatWindow;
	private TeamCombatDisplayController teamADisplay;
	private TeamCombatDisplayController teamBDisplay;
	private CombatLog combatTextDisplay;
	private Dialog errorMessage;
	
	//Scene2d ui
	private Stage ui;
	private Table table;
	
	//Combat event and state variables
	private BattleContext battleContext;
	
	private CreatureTeam teamA;
	private CreatureTeam teamB;
	
	public TestCombatScreen(AlchemonstersGame game) {
		super(game);
	}
	
	@Override
	public void show() {
		teamA = new CreatureTeam();
		//battleTeamA = new BattleTeam(teamA, 2);
		teamB = new CreatureTeam();
		//battleTeamB = new BattleTeam(teamB, 2);
		
		ui = new Stage(game.uiView, game.batch);
		table = new Table(UI.DEFAULT_SKIN);
		table.setFillParent(true);
		ui.addActor(table);
		table.bottom();
		
		Table teamBuilders = new Table();
		teamABuilder = new TeamBuilderWindow(ui, "Team A", teamA);
		teamBBuilder = new TeamBuilderWindow(ui, "Team B", teamB);
		teamBuilders.add(teamABuilder).expandY().fill().prefWidth(300);
		teamBuilders.row();
		teamBuilders.add(teamBBuilder).expandY().fill().prefWidth(300);
		teamBuilders.row();
		
		errorMessage = new Dialog("Error", UI.DEFAULT_SKIN) {
			@Override
			protected void result(Object object) {
				boolean close = (boolean) object;
				if(close) {
					hide();
				}
			}
		};
		errorMessage.text("Unable to start combat with empty team.\nPlease add at least one mon to each team.");
		errorMessage.button("Close", true);
		
		Window menuWindow = new Window("Options", UI.DEFAULT_SKIN);
		menuWindow.center();
		
		
		
		TextButton start1v1 = new TextButton("Start 1v1", UI.DEFAULT_SKIN);
		start1v1.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startCombat(1);
			}
		});
		TextButton start2v2 = new TextButton("Start 2v2", UI.DEFAULT_SKIN);
		start2v2.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startCombat(2);
			}
		});
		menuWindow.add(start1v1).expandX().fillX();
		menuWindow.add(start2v2).expandX().fillX();
		menuWindow.row();
		
		TextButton mainMenuButton = new TextButton("Exit to Main Menu", UI.DEFAULT_SKIN);
		mainMenuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new MainMenuScreen(game));
			}
		});
		menuWindow.add(mainMenuButton);
		menuWindow.row();
		
		teamBuilders.add(menuWindow).fill();
		
		table.add(teamBuilders).expandY().fill().align(Align.topLeft).prefWidth(300);
		
		//COMBAT WINDOW SETUP
		combatWindow = new Window("Combat Display", UI.DEFAULT_SKIN);
		combatWindow.setMovable(false);
		
		teamADisplay = new TeamCombatDisplayController("Team A");
		teamBDisplay = new TeamCombatDisplayController("Team B");
		
		combatTextDisplay = new CombatLog();
		
		Table teamDisplayTable = new Table();
		teamDisplayTable.add(teamADisplay).expand().fill().left();
		teamDisplayTable.add(teamBDisplay).expand().fill().right();
		combatWindow.add(teamDisplayTable).expandX().fill();
		combatWindow.row();
		combatWindow.add(combatTextDisplay).expand().fill().padTop(20);
		
		table.add(combatWindow).expand().fill();
		
		InputMultiplexer multi = new InputMultiplexer(ui, this);
		Gdx.input.setInputProcessor(multi);
		
		subscribe(MCombatStateChanged.ID);
		subscribe(MCombatFinished.ID);
		subscribe(MCombatStarted.ID);
	}	

	private void startCombat(int positions) {
		BattleTeam battleTeamA = new BattleTeam(teamA, positions);
		BattleTeam battleTeamB = new BattleTeam(teamB, positions);
		
		//Make sure that there is at least one mon to be active for each team.
		if(teamA.getNumCreatures() == 0 || teamB.getNumCreatures() == 0) {
			errorMessage.show(ui);
			return;
		}
		
		int activeFillA = Math.min(positions, teamA.getNumCreatures());
		int activeFillB = Math.min(positions, teamB.getNumCreatures());
		
		//For every active position
		for(int i = 0; i < activeFillA; ++i) {
			//If the position is empty
			if(battleTeamA.get(i) == null) {
				//Look through all other positions
				for(int k = i+1; k < CreatureTeam.TEAM_SIZE; ++k) {
					//And if you find a filled position, swap that creature to the active slot
					if(battleTeamA.get(k) != null) {
						battleTeamA.swap(i, k);
					}
				}
			}
		}
		
		for(int i = 0; i < activeFillB; ++i) {
			if(battleTeamB.get(i) == null) {
				for(int k = i+1; k < CreatureTeam.TEAM_SIZE; ++k) {
					if(battleTeamB.get(k) != null) {
						battleTeamB.swap(i, k);
					}
				}
			}
		}
		
		//Setup display windows
		teamADisplay.setup(battleTeamA, battleTeamB, (new TeamController(positions)).getControls(), ui);
		teamBDisplay.setup(battleTeamB, battleTeamA, (new TeamController(positions)).getControls(), ui);
		
		//Combat setup
		battleContext = new BattleContext(
				battleTeamA, teamADisplay.getControllers(), 
				battleTeamB, teamBDisplay.getControllers());
		
		//Reset health and mana values
		for(Creature c : battleTeamA.creatures()) {
			if(c != null) c.resetHealthAndMana();
		}
		for(Creature c : battleTeamB.creatures()) {
			if(c != null) c.resetHealthAndMana();
		}
		
		//Reset to default battleground values
		battleContext.battleground.terrain = TerrainType.NORMAL;
		battleContext.battleground.biome = BiomeType.NORMAL;
		battleContext.battleground.weather = WeatherType.NORMAL;
		
		combatTextDisplay.clear();
		battleContext.startCombat();
		
		teamADisplay.updateStrings();
		teamBDisplay.updateStrings();
	}
	
	@Override
	public void handleMessage(Message currentMessage) {
		teamADisplay.updateStrings();
		teamBDisplay.updateStrings();
	}
	
	@Override
	public void renderGraphics(float delta) {
		//no graphics for this screen
	}
	
	@Override
	public void renderUI(float delta) {		

		if(battleContext != null) {
			battleContext.updateBattle();
			teamADisplay.updateStrings();
			teamBDisplay.updateStrings();
		}
		
		ui.act(delta);
		ui.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		table.invalidate();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}
	
	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		ui.dispose();
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		switch(keycode) {
		
		case Keys.F5:
			GameData.initAndLoad();
			ScriptManager.init();
			ScriptDatabase.initAndLoad();
			CreatureDatabase.initAndLoad();
			MoveDatabase.initAndLoad();
			
			game.setScreen(new MainMenuScreen(game));
			return true;
		
		case Keys.F1:
			return true;
			
		}
		
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
