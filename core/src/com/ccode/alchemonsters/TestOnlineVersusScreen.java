package com.ccode.alchemonsters;

import java.io.IOException;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.engine.GameScreen;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.net.ClientTeamController;
import com.ccode.alchemonsters.net.ClientUnitController;
import com.ccode.alchemonsters.net.KryoCreator;
import com.ccode.alchemonsters.net.NetBattleContextUpdate;
import com.ccode.alchemonsters.net.NetErrorMessage;
import com.ccode.alchemonsters.net.NetFilterAllActions;
import com.ccode.alchemonsters.net.NetFilterAvailableActions;
import com.ccode.alchemonsters.net.NetJoinSuccess;
import com.ccode.alchemonsters.net.NetJoinVersus;
import com.ccode.alchemonsters.net.NetRefreshControl;
import com.ccode.alchemonsters.net.NetResetAvailableActions;
import com.ccode.alchemonsters.net.NetSetActions;
import com.ccode.alchemonsters.ui.CombatLog;
import com.ccode.alchemonsters.ui.TeamBuilderWindow;
import com.ccode.alchemonsters.ui.TeamCombatDisplay;
import com.ccode.alchemonsters.ui.TeamCombatDisplayController;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class TestOnlineVersusScreen extends GameScreen implements InputProcessor, Publisher {

	private static final String UUID_NAME = "_MULTIPLAYER_UUID";
	
	private UUID myID;
	
	//Overall frame
	private TeamBuilderWindow teamBuilder;
	private TextField ipInput;
	
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
	private boolean isCombatStarted = false;
	
	public TestOnlineVersusScreen(AlchemonstersGame game) {
		super(game);
	}

	@Override
	public void show() {
		myID = UUID.randomUUID();
		
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
		
		ipInput = new TextField("localhost", UI.DEFAULT_SKIN);
		menuWindow.add(ipInput).expandX().fillX();
		menuWindow.row();
		
		Table startButtons = new Table();
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
		startButtons.add(start1v1).expandX().fillX();
		startButtons.add(start2v2).expandX().fillX();
		menuWindow.add(startButtons).expandX().fillX();
		menuWindow.row();
		
		TextButton mainMenuButton = new TextButton("Exit to Main Menu", UI.DEFAULT_SKIN);
		mainMenuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//TODO: exit combat and close connection
				game.setScreen(new MainMenuScreen(game));
			}
		});
		menuWindow.add(mainMenuButton).expandX().fillX();
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
		combatWindow.add(teamDisplayTable).expandX().fillX();
		combatWindow.row();
		combatWindow.add(combatTextDisplay).expand().fill().padTop(20);
		
		table.add(combatWindow).expand().fill();
		
		InputMultiplexer multi = new InputMultiplexer(ui, this);
		Gdx.input.setInputProcessor(multi);
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
				client.connect(5000, ipInput.getText(), 48372);
				
				BattleTeam team = new BattleTeam(myTeam, numActives);
				team.variables.setVariable(UUID_NAME, myID);
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
	
	private void showInfoMessage(String s) {
		Dialog message = new Dialog("Info", UI.DEFAULT_SKIN);
		message.text(s);
		message.button("OK");
		message.show(ui);
	}
	
	@Override
	public void renderGraphics(float delta) {
		if(client != null) {
			try {
				client.update(0);
			} catch (IOException e) {}
		}
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
			
			//System.out.println(object);
			
			if(object instanceof NetJoinSuccess) {
				if(isWaitingForJoin) {
					showInfoMessage("Connected!");
				}
				else {
					//TODO: wasn't waiting for join, so we probably need error handling here
				}
			}
			
			else if(object instanceof NetErrorMessage) {
				handleErrorMessage((NetErrorMessage) object);
			}
			
			else if(object instanceof Message) {
				publish((Message) object); 
				handleMessage((Message) object);
			}
			
			else if(object instanceof NetSetActions) {
				NetSetActions full = (NetSetActions) object;
				myControls[full.activePos].setAllActions(full.actions);
				myTeamDisplay.updateStrings();
			}
			
			else if(object instanceof NetFilterAllActions) {
				NetFilterAllActions full = (NetFilterAllActions) object;
				myControls[full.activePos].filterAllActions(full.filter);
				myTeamDisplay.updateStrings();
			}
			
			else if(object instanceof NetFilterAvailableActions) {
				NetFilterAvailableActions full = (NetFilterAvailableActions) object;
				myControls[full.activePos].filterAvailableActions(full.filter);
				myTeamDisplay.updateStrings();
			}
			
			else if(object instanceof NetResetAvailableActions) {
				NetResetAvailableActions full = (NetResetAvailableActions) object;
				myControls[full.activePos].resetAvailableActions();
				myTeamDisplay.updateStrings();
			}
			
			else if(object instanceof NetRefreshControl) {
				NetRefreshControl full = (NetRefreshControl) object;
				myControls[full.activePos].refresh();
				myTeamDisplay.updateStrings();
			}
			
			else if(object instanceof NetBattleContextUpdate) {
				updateContext(((NetBattleContextUpdate) object).context);
			}
			
		}
		
		@Override
		public void disconnected(Connection connection) {
			
		}
		
		private void updateContext(BattleContext context) {
			if(context.teamA.variables.getAs(UUID.class, UUID_NAME).equals(myID)) {
				myTeamDisplay.setTeam(context.teamA);
				theirTeamDisplay.setTeam(context.teamB);
			}
			else if(context.teamB.variables.getAs(UUID.class, UUID_NAME).equals(myID)) {
				myTeamDisplay.setTeam(context.teamB);
				theirTeamDisplay.setTeam(context.teamA);
			}
			myTeamDisplay.updateStrings();
			theirTeamDisplay.updateStrings();
		}
		
		private void handleMessage(Message m) {
			if(m instanceof MCombatStarted) {
				MCombatStarted full = (MCombatStarted) m;
				if(full.teamA.variables.getAs(UUID.class, UUID_NAME).equals(myID)) {
					myTeamDisplay.setup(full.teamA, myControls, ui);
					theirTeamDisplay.setup(full.teamB, ui);
				}
				else if(full.teamB.variables.getAs(UUID.class, UUID_NAME).equals(myID)) {
					myTeamDisplay.setup(full.teamB, myControls, ui);
					theirTeamDisplay.setup(full.teamA, ui);
				}
				else {
					//TODO: should never happen
					showErrorMessage("No team with matching UUID!");
					client.close();
				}
			}
		}
		
		private void handleErrorMessage(NetErrorMessage err) {
			switch(err.errno) {
			
			case NetErrorMessage.ERR_JOIN_ERROR:
				if(isWaitingForJoin) {
					showErrorMessage(err.message);
					System.out.println(err.message);
					isWaitingForJoin = false;
				}
				break;
				
			case NetErrorMessage.ERR_LOBBY_FULL:
				if(isWaitingForJoin) {
					showErrorMessage(err.message);
					System.out.println("Error, lobby full.");
					client.close();
				}
				break;
			
			}
		}
		
	}

}




















