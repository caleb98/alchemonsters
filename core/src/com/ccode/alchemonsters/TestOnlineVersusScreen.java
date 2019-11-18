package com.ccode.alchemonsters;

import java.io.IOException;

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
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.engine.GameScreen;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.event.ListSubscriber;
import com.ccode.alchemonsters.net.ClientTeamController;
import com.ccode.alchemonsters.net.ClientUnitController;
import com.ccode.alchemonsters.net.KryoCreator;
import com.ccode.alchemonsters.net.NetErrorMessage;
import com.ccode.alchemonsters.net.NetJoinSuccess;
import com.ccode.alchemonsters.net.NetJoinVersus;
import com.ccode.alchemonsters.ui.CombatLog;
import com.ccode.alchemonsters.ui.TeamBuilderWindow;
import com.ccode.alchemonsters.ui.TeamCombatDisplay;
import com.ccode.alchemonsters.ui.TeamCombatDisplayController;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

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
	
	//Scene2d ui
	private Stage ui;
	private Table table;
	
	//Team
	private CreatureTeam myTeam;
	private ClientUnitController[] myControls;
	
	//Battle state stuff
	private BattleContext battleContext;
	
	//Network
	private Client client;
	private boolean isWaitingForJoin = false;
	
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
		start1v1.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startConnection(1);
			}
		});
		TextButton start2v2 = new TextButton("Start 2v2", UI.DEFAULT_SKIN);
		start2v2.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startConnection(2);
			}
		});
		menuWindow.add(start1v1).expandX().fillX();
		menuWindow.add(start2v2).expandX().fillX();
		menuWindow.row();
		
		TextButton mainMenuButton = new TextButton("Exit to Main Menu", UI.DEFAULT_SKIN);
		mainMenuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//TODO: exit combat and close connection
				game.setScreen(new MainMenuScreen(game));
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
		
		InputMultiplexer multi = new InputMultiplexer(ui, this);
		Gdx.input.setInputProcessor(multi);
		
		sub = new ListSubscriber();
		
	}
	
	private void startConnection(int numActives) {
		//Check that there is at least one creature
		if(myTeam.getNumCreatures() == 0) {
			showErrorMessage("Team needs at least one creature to start battle.");
			return;
		}
		
		//Move as many creatures into active positions as possible
		for(int i = 0; i < numActives; ++i) {
			if(myTeam.creatures[i] == null) {
				for(int k = numActives; k < CreatureTeam.TEAM_SIZE; ++k) {
					if(myTeam.creatures[k] != null) {
						myTeam.swap(k, i);
						break;
					}
				}
			}
		}
		
		//Start connections
		(new Thread(()->{
			try {
				client = KryoCreator.createClient();
				client.addListener(new VersusClientListener());
				client.start();
				client.connect(5000, "localhost", 48372);
				
				BattleTeam team = new BattleTeam(myTeam, numActives);
				myControls = new ClientTeamController(client, numActives).getControls();
				
				client.sendTCP(new NetJoinVersus("Team", team, numActives));
				isWaitingForJoin = true;
			} catch (IOException e) {
				//Unable to connect
				showErrorMessage("Unable to connect to server.");
				System.out.println(e.getMessage());
			}
		})).start();
	}
	
	private void showErrorMessage(String s) {
		Dialog error = new Dialog("Error", UI.DEFAULT_SKIN);
		error.text(s);
		error.button("OK");
		error.show(ui);
	}
	
	@Override
	public void renderGraphics(float delta) {

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
	
	private class VersusClientListener extends Listener {
		
		@Override
		public void received(Connection connection, Object object) {
			if(object instanceof NetJoinSuccess) {
				if(isWaitingForJoin) {
					System.out.println("we're in!");
				}
				else {
					//TODO: wasn't waiting for join, so we probably need error handling here
				}
			}
			else if(object instanceof NetErrorMessage) {
				handleErrorMessage((NetErrorMessage) object);
			}
		}
		
		@Override
		public void disconnected(Connection connection) {
			
		}
		
		private void handleErrorMessage(NetErrorMessage err) {
			switch(err.errno) {
			
			case NetErrorMessage.ERR_JOIN_ERROR:
				if(isWaitingForJoin) {
					System.out.println(err.message);
					isWaitingForJoin = false;
				}
				break;
				
			case NetErrorMessage.ERR_LOBBY_FULL:
				if(isWaitingForJoin)
				System.out.println("Error, lobby full.");
				break;
			
			}
		}
		
	}

}




















