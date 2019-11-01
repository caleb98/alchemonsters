package com.ccode.alchemonsters;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleAction.BattleActionType;
import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleController;
import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.GroundType;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.combat.moves.MoveAction;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.GameScreen;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.event.ListSubscriber;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatStateChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;
import com.ccode.alchemonsters.ui.CombatConsole;
import com.ccode.alchemonsters.ui.CreatureEditWindow;
import com.ccode.alchemonsters.ui.TeamBuilderWindow;
import com.ccode.alchemonsters.ui.TeamCombatDisplay;
import com.ccode.alchemonsters.util.GameRandom;

public class TestCombatScreen extends GameScreen implements InputProcessor, Screen, Publisher {
	
	private CreatureEditWindow creatureEdit;
	private CreatureTeam currentTeam;
	private int currentId;
	
	//Message listener
	private ListSubscriber sub;
	
	//Overall Frame
	private TeamBuilderWindow teamABuilder;
	private TeamBuilderWindow teamBBuilder;
	
//	private Label teamATitle;
//	private Label teamA1;
//	private TextButton teamA1Edit;
//	private Label teamA2;
//	private TextButton teamA2Edit;
//	private Label teamA3;
//	private TextButton teamA3Edit;
//	private Label teamA4;
//	private TextButton teamA4Edit;
//	
//	private Label teamBTitle;
//	private Label teamB1;
//	private TextButton teamB1Edit;
//	private Label teamB2;
//	private TextButton teamB2Edit;
//	private Label teamB3;
//	private TextButton teamB3Edit;
//	private Label teamB4;
//	private TextButton teamB4Edit;
	
	//TODO: Combat window UI
	private Window combatWindow;
	private TeamCombatDisplay teamADisplay;
	private TeamCombatDisplay teamBDisplay;
	private CombatConsole combatTextDisplay;
	
	//Scene2d ui
	private Stage ui;
	private Table table;
	
	//Combat event and state variables
	private BattleContext battleContext;
	private boolean isCombat = false;
	
	private BattleController teamAControl;
	private CreatureTeam teamA;
	
	private BattleController teamBControl;
	private CreatureTeam teamB;
	
	private ArrayList<DelayedMoveInfo> delayedMoves = new ArrayList<>();
	
	private boolean isWaitingOnActionSelect = false;
	private boolean isWaitingOnDeathSwitchSelect = false;
	private boolean isTeamADoubleAttack = false;
	private boolean isTeamBDoubleAttack = false;
	
	public TestCombatScreen(AlchemonstersGame game) {
		super(game);
	}
	
	@Override
	public void show() {
		teamA = new CreatureTeam();
		teamB = new CreatureTeam();
		
		ui = new Stage(game.uiView, game.batch);
		table = new Table(UI.DEFAULT_SKIN);
		table.setFillParent(true);
		ui.addActor(table);
		table.bottom();
		
//		Window teamBuilder = new Window("Team Setup", UI.DEFAULT_SKIN);
//		teamBuilder.top();
//		teamBuilder.setMovable(false);
//		table.add(teamBuilder).expandY().left().top().fillY().prefWidth(300);
		
		Table teamBuilders = new Table();
		teamABuilder = new TeamBuilderWindow(ui, "Team A", teamA);
		teamBBuilder = new TeamBuilderWindow(ui, "Team B", teamB);
		teamBuilders.add(teamABuilder).expandY().fill().prefWidth(300);
		teamBuilders.row();
		teamBuilders.add(teamBBuilder).expandY().fill().prefWidth(300);
		
		table.add(teamBuilders).expandY().fill().align(Align.topLeft).prefWidth(300);
		
		Dialog errorMessage = new Dialog("Error", UI.DEFAULT_SKIN) {
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
		
		TextButton startCombatButton = new TextButton("Start Combat", UI.DEFAULT_SKIN);
		startCombatButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(teamA.active() == null) {
					teamA.active = -1;
					for(int i = 1; i < teamA.creatures.length; ++i) {
						if(teamA.creatures[i] != null) {
							teamA.active = i;
							break;
						}
					}
					if(teamA.active == -1) {
						teamA.active = 0;
						errorMessage.show(ui);
						return;
					}
				}
				if(teamB.active() == null) {
					teamB.active = -1;
					for(int i = 1; i < teamB.creatures.length; ++i) {
						if(teamB.creatures[i] != null) {
							teamB.active = i;
							break;
						}
					}
					if(teamB.active == -1) {
						teamB.active = 0;
						errorMessage.show(ui);
						return;
					}
				}
				startCombat();
			}
		});
		//teamBuilder.add(startCombatButton).padTop(20);
		//teamBuilder.row();
		
		TextButton mainMenuButton = new TextButton("Exit to Main Menu", UI.DEFAULT_SKIN);
		mainMenuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new MainMenuScreen(game));
			}
		});
		//teamBuilder.add(mainMenuButton).expandY().bottom().left();
		//teamBuilder.row();
		
		creatureEdit = new CreatureEditWindow(ui);
		creatureEdit.setVisible(false);
		creatureEdit.addAcceptListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(creatureEdit.isEditComplete()) {
					currentTeam.creatures[currentId] = creatureEdit.getEditedCreature();
				}
			}
		});
		ui.addActor(creatureEdit);
		
		//COMBAT WINDOW SETUP
		combatWindow = new Window("Combat Display", UI.DEFAULT_SKIN);
		combatWindow.setMovable(false);
		
		teamADisplay = new TeamCombatDisplay("Team A", teamA);
		teamAControl = teamADisplay;
		teamBDisplay = new TeamCombatDisplay("Team B", teamB);
		teamBControl = teamBDisplay;
		
		combatTextDisplay = new CombatConsole();
		
		combatWindow.add(teamADisplay).expand().fillY();
		combatWindow.add(combatTextDisplay).expand().fill().minWidth(400);
		combatWindow.add(teamBDisplay).expand().fillY();
		
		table.add(combatWindow).expand().fill();
		
		InputMultiplexer multi = new InputMultiplexer(ui, this);
		Gdx.input.setInputProcessor(multi);
		
		sub = new ListSubscriber();
		sub.subscribe(MCombatStateChanged.ID);
	}	
	
	private void displayCreatureEditWindow(CreatureTeam t, int id) {
		currentTeam = t;
		currentId = id;
		creatureEdit.show(t.creatures[id]);
	}

	private void startCombat() {
		//Combat setup
		battleContext = new BattleContext(teamAControl, teamA, teamBControl, teamB);
		
		teamA.startCombat();
		teamB.startCombat();
		
		battleContext.teamA = teamA;
		battleContext.teamB = teamB;
		
		isCombat = true;
		
		//Reset health and mana values
		for(Creature c : teamA.creatures) {
			if(c != null) c.rest();
		}
		for(Creature c : teamB.creatures) {
			if(c != null) c.rest();
		}
		
		//Reset to default battleground values
		battleContext.battleground.ground = GroundType.NORMAL;
		battleContext.battleground.terrain = TerrainType.NORMAL;
		battleContext.battleground.weather = WeatherType.NORMAL;
		
		combatTextDisplay.clear();
		publish(new MCombatStarted(battleContext, teamA, teamB));
		setCombatState(CombatState.MAIN_PHASE_1);
	}
	
	private void setCombatState(CombatState next) {
		MCombatStateChanged message = new MCombatStateChanged(battleContext, battleContext.currentState, next);
		battleContext.currentState = next;
		publish(message);
	}
	
	private void doBattleAction(BattleController control, CreatureTeam team, CreatureTeam other) {
		if(team.active().isDead() && control.getSelectedAction().type != BattleActionType.SWITCH) {
			return;
		}
		
		BattleAction action = control.getSelectedAction();
		switch(action.type) {
		
		case MOVE:
			String moveName = team.active().moves[action.id];
			Move move = MoveDatabase.getMove(moveName);
			team.active().currentMana -= move.manaCost;
			switch(move.turnType) {
			
			case CHARGE:
				//First check for dreamscape
				if(battleContext.battleground.weather == WeatherType.DREAMSCAPE) {
					publish(new MCombatChargeStarted(battleContext, team.active(), other.active(), move));
					for(MoveAction a : move.actions) {
						a.activate(move, battleContext, team.active(), team, other.active(), other);
					}
					team.active().variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(battleContext, team.active(), other.active(), move));
				}
				else if(!control.isCharging()) {
					control.setCharging(action.id);
					publish(new MCombatChargeStarted(battleContext, team.active(), other.active(), move));
				}
				else {
					control.stopCharging();
					for(MoveAction a : move.actions) {
						a.activate(move, battleContext, team.active(), team, other.active(), other);
					}
					team.active().variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(battleContext, team.active(), other.active(), move));
				}
				break;
				
			case DELAYED:
				delayedMoves.add(new DelayedMoveInfo(team, team.active(), move, other, move.delayAmount));
				break;
				
			case RECHARGE:
				//Deluge makes it so that recharge abilities dont require a recharge time,
				//so only apply the recharge if the current weather is NOT deluge
				if(battleContext.battleground.weather != WeatherType.DELUGE) {
					control.setRecharging(true);	
				}
			case INSTANT:
				for(MoveAction a : move.actions) {
					a.activate(move, battleContext, team.active(), team, other.active(), other);
				}
				team.active().variables.setVariable("_PREVIOUS_MOVE", move);
				break;
			
			}
			break;
			
		case SWITCH:
			MCombatTeamActiveChanged message = new MCombatTeamActiveChanged(battleContext, control, team, team.active, action.id);
			team.active = action.id;
			publish(message);
			break;
			
		case USE:
			//TODO: implement inventory and item use
			break;
			
		case WAIT:
			control.setRecharging(false);
			break;
		
		}
	}
	
	@Override
	public void renderGraphics(float delta) {
		//no graphics for this screen
	}
	
	@Override
	public void renderUI(float delta) {
		
		Message m;
		while((m = sub.messageQueue.poll()) != null) {
			if(m instanceof MCombatStateChanged) {
				MCombatStateChanged full = (MCombatStateChanged) m;
				switch(full.next) {
				
				case BATTLE_PHASE_1:
					doBattlePhase();
					break;
					
				case BATTLE_PHASE_2:
					if(isTeamADoubleAttack) {
						doBattleAction(teamAControl, teamA, teamB);
					}
					else if(isTeamBDoubleAttack) {
						doBattleAction(teamBControl, teamB, teamA);
					}
					if(teamA.isDefeated()) {
						publish(new MCombatFinished(battleContext, teamB, teamA));
						battleContext.endCombat();
						isCombat = false;
						return;
					}
					else if(teamB.isDefeated()) {
						publish(new MCombatFinished(battleContext, teamA, teamB));
						battleContext.endCombat();
						isCombat = false;
						return;
					}
					else {
						setCombatState(CombatState.END_PHASE);	
					}
					break;
					
				case END_PHASE:
					if(!areActivesDead()) {
						//TODO: speed double hit check
						setCombatState(CombatState.MAIN_PHASE_1);
					}
					else {
						setCombatState(CombatState.ACTIVE_DEATH_SWAP);
					}
					break;
					
				case MAIN_PHASE_1:
					//Apply damage from sandstorm
					if(battleContext.battleground.weather == WeatherType.SANDSTORM) {
						
						for(ElementType t : teamA.active().base.types) {
							if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
								teamA.active().currentHealth -= teamA.active().maxHealth / 16f;
								break;
							}
						}
						
						for(ElementType t : teamB.active().base.types) {
							if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
								teamB.active().currentHealth -= teamB.active().maxHealth / 16f;
								break;
							}
						}
						
						//Check for sandstorm death
						if(areActivesDead()) {
							if(teamA.isDefeated()) {
								publish(new MCombatFinished(battleContext, teamB, teamA));
								battleContext.endCombat();
								isCombat = false;
								break;
							}
							else if(teamB.isDefeated()) {
								publish(new MCombatFinished(battleContext, teamA, teamB));
								battleContext.endCombat();
								isCombat = false;
								break;
							}
							else {
								setCombatState(CombatState.ACTIVE_DEATH_SWAP);
								break;
							}
						}
						
					}
					
					//Apply healing from dreamscape if applicable
					if(battleContext.battleground.weather == WeatherType.DREAMSCAPE) {
						
						teamA.active().currentHealth += teamA.active().maxHealth / 16f;
						if(teamA.active().currentHealth > teamA.active().maxHealth) 
							teamA.active().currentHealth = teamA.active().maxHealth;
						
						teamB.active().currentHealth += teamB.active().maxHealth / 16f;
						if(teamB.active().currentHealth > teamB.active().maxHealth) 
							teamB.active().currentHealth = teamB.active().maxHealth;
						
					}
					
					//Apply healing from tempest if applicable
					if(battleContext.battleground.weather == WeatherType.TEMPEST) {
						
						for(ElementType t : teamA.active().base.types) {
							if(t == ElementType.UNDEAD) {
								teamA.active().currentHealth += teamA.active().maxHealth / 16f;
								if(teamA.active().currentHealth > teamA.active().maxHealth) 
									teamA.active().currentHealth = teamA.active().maxHealth;
								break;
							}
						}
						
						for(ElementType t : teamB.active().base.types) {
							if(t == ElementType.UNDEAD) {
								teamB.active().currentHealth += teamB.active().maxHealth / 16f;
								if(teamB.active().currentHealth > teamB.active().maxHealth) 
									teamB.active().currentHealth = teamB.active().maxHealth;
								break;
							}
						}
						
					}
					
					isWaitingOnActionSelect = true;
					setDefaultControllerActions(teamAControl, teamA);
					setDefaultControllerActions(teamBControl, teamB);
					teamAControl.refresh();
					teamBControl.refresh();
					break;
					
				case MAIN_PHASE_2:
					//TODO: main phase if available
					break;
					
				case ACTIVE_DEATH_SWAP:
					isWaitingOnDeathSwitchSelect = true;
					
					if(teamA.active().isDead()) {
						setDefaultControllerActions(teamAControl, teamA);
						teamAControl.refresh();
						teamAControl.filterActions((a)->{return a.type != BattleActionType.SWITCH;});
					}
					
					if(teamB.active().isDead()) {
						setDefaultControllerActions(teamBControl, teamB);
						teamBControl.refresh();
						teamBControl.filterActions((a)->{return a.type != BattleActionType.SWITCH;});
					}
					break;
					
				default:
					break;
				
				}
			}
		}
		
		if(isWaitingOnActionSelect) {
			if(teamAControl.isActionSelected() && teamBControl.isActionSelected()) {
				isWaitingOnActionSelect = false;
				if(battleContext.currentState == CombatState.MAIN_PHASE_1)
					setCombatState(CombatState.BATTLE_PHASE_1);
				else if(battleContext.currentState == CombatState.MAIN_PHASE_2)
					setCombatState(CombatState.BATTLE_PHASE_2);
			}
		}
		else if(isWaitingOnDeathSwitchSelect) {
			if(teamAControl.isActionSelected() && teamBControl.isActionSelected()) {
				isWaitingOnDeathSwitchSelect = false;
				if(teamA.active().isDead()) {
					doBattleAction(teamAControl, teamA, teamB);
				}
				if(teamB.active().isDead()) {
					doBattleAction(teamBControl, teamB, teamA);
				}
				setCombatState(CombatState.MAIN_PHASE_1);
			}
		}
		
		if(isCombat) {
			battleContext.update();
		}
		
		ui.act(delta);
		ui.draw();
		
	}
	
	private boolean areActivesDead() {
		boolean activesDead = false;
		
		if(teamA.active().isDead()) {
			activesDead = true;
		}
		if(teamB.active().isDead()) {
			activesDead = true;
		}
		
		return activesDead;
	}
	
	private void setDefaultControllerActions(BattleController control, CreatureTeam team) {		
		ArrayList<BattleAction> actions = new ArrayList<>();
		
		//Check for special charging case - we'll need different actions
		if(control.isCharging()) {
			actions.add(new BattleAction(BattleActionType.MOVE, control.getCharging()));
			control.setAvailableActions(actions);
			return;
		}
		
		if(control.isRecharging()) {
			actions.add(new BattleAction(BattleActionType.WAIT, 0));
			control.setAvailableActions(actions);
			return;
		}
		
		for(int i = 0; i < team.creatures.length; ++i) {
			if(i != team.active && team.creatures[i] != null && !team.creatures[i].isDead()) {
				actions.add(new BattleAction(BattleActionType.SWITCH, i));
			}
		}
		for(int i = 0; i < team.active().moves.length; ++i) {
			Move move = MoveDatabase.getMove(team.active().moves[i]);
			if(team.active().currentMana >= move.manaCost) {
				actions.add(new BattleAction(BattleActionType.MOVE, i));
			}
		}
		actions.add(new BattleAction(BattleActionType.WAIT, 0));
		control.setAvailableActions(actions);
	}
	
	private void doBattlePhase() {
		BattleAction teamAAction = teamAControl.getSelectedAction();
		BattleAction teamBAction = teamBControl.getSelectedAction();
		
		if(teamAAction.type == BattleActionType.MOVE && teamBAction.type == BattleActionType.MOVE) {
			Move teamAMove = MoveDatabase.getMove(teamA.active().moves[teamAAction.id]);
			Move teamBMove = MoveDatabase.getMove(teamB.active().moves[teamBAction.id]);
			
			int teamAOrder = teamAMove.priority;
			int teamBOrder = teamBMove.priority;
			
			if(teamAOrder == teamBOrder) {
				teamAOrder = teamA.active().calcTotalSpeed();
				teamBOrder = teamB.active().calcTotalSpeed();
			}
			
			if(teamAOrder == teamBOrder) {
				int rand = GameRandom.nextBoolean() ? 1 : -1;
				teamAOrder = rand;
				teamBOrder = -rand;
			}
			
			if(teamAOrder > teamBOrder) {
				doBattleAction(teamAControl, teamA, teamB);
				doBattleAction(teamBControl, teamB, teamA);
			}
			else {
				doBattleAction(teamBControl, teamB, teamA);
				doBattleAction(teamAControl, teamA, teamB);
			}
			
		}
		else if(teamAAction.compareTo(teamBAction) < 0) {
			doBattleAction(teamAControl, teamA, teamB);
			doBattleAction(teamBControl, teamB, teamA);
		}
		else {
			doBattleAction(teamBControl, teamB, teamA);
			doBattleAction(teamAControl, teamA, teamB);
		}
		
		//Do delayed moves
		Iterator<DelayedMoveInfo> delays = delayedMoves.iterator();
		while(delays.hasNext()) {
			DelayedMoveInfo inf = delays.next();
			if(inf.delayTurns == 0) {
				Move move = inf.move;
				for(MoveAction a : move.actions) {
					a.activate(move, battleContext, inf.sourceCreature, inf.sourceTeam, inf.targetTeam.active(), inf.targetTeam);
				}
				inf.sourceCreature.variables.setVariable("_PREVIOUS_MOVE", move);
				delays.remove();
			}
			else {
				inf.delayTurns--;
			}
		}
		
		if(teamA.isDefeated()) {
			publish(new MCombatFinished(battleContext, teamB, teamA));
			battleContext.endCombat();
			isCombat = false;
			return;
		}
		else if(teamB.isDefeated()) {
			publish(new MCombatFinished(battleContext, teamA, teamB));
			battleContext.endCombat();
			isCombat = false;
			return;
		}
		
		//check for speed double attack
		if(teamAAction.type == BattleActionType.MOVE &&
		   teamA.active().calcTotalSpeed() >= 2 * teamB.active().calcTotalSpeed()) {
			
			isWaitingOnActionSelect = true;
			//TODO: filter actions that cant be used for second attack?
			setDefaultControllerActions(teamAControl, teamA);
			teamAControl.refresh();
			isTeamADoubleAttack = true;
			setCombatState(CombatState.MAIN_PHASE_2);
			
		}
		else if(teamBAction.type == BattleActionType.MOVE &&
				teamB.active().calcTotalSpeed() >= 2 * teamA.active().calcTotalSpeed()) {
			
			isWaitingOnActionSelect = true;
			//TODO: filter actions that cant be used for second attack?
			setDefaultControllerActions(teamBControl, teamB);
			teamBControl.refresh();
			isTeamBDoubleAttack = true;
			setCombatState(CombatState.MAIN_PHASE_2);
			
		}
		else {
			setCombatState(CombatState.END_PHASE);	
		}
	}

	@Override
	public void resize(int width, int height) {
		ui.getViewport().update(width, height, true);
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
		if(keycode == Keys.F1) {
			table.setDebug(!table.getDebug(), true);
			creatureEdit.setDebug(!creatureEdit.getDebug(), true);
		}
		else if(keycode == Keys.P) {
			Json json = new Json();
			json.setOutputType(OutputType.javascript);
			System.out.println(json.prettyPrint(currentTeam.creatures[currentId]));
		}
		else if(keycode == Keys.F5) {
			MoveDatabase.initAndLoad();
			creatureEdit.reloadMovesList();
		}
		else {
			return false;
		}
		return true;
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
	
	private class DelayedMoveInfo {
		
		CreatureTeam sourceTeam;
		Creature sourceCreature;
		Move move;
		CreatureTeam targetTeam;
		int delayTurns;
		
		public DelayedMoveInfo(CreatureTeam team, Creature creature, Move move, CreatureTeam target, int turns) {
			sourceTeam = team;
			sourceCreature = creature;
			this.move = move;
			targetTeam = target;
			delayTurns = turns;
		}
		
	}
	
	private class EditButton extends TextButton {
		
		EditButton(CreatureTeam t, int id) {
			super("Edit / Add", UI.DEFAULT_SKIN);
			addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					displayCreatureEditWindow(t, id);
				}
			});
		}
		
	}
	
	private class CreatureNameLabel extends Label {
		
		CreatureTeam t;
		int id;
		
		CreatureNameLabel(CreatureTeam t, int id) {
			super("<empty>", UI.DEFAULT_SKIN);
			this.t = t;
			this.id = id;
		}
		
		@Override
		public void act(float delta) {
			super.act(delta);
			if(t.creatures[id] == null) {
				setText("<empty>");
			}
			else if(!t.creatures[id].personalName.equals(getText())) {
				setText(t.creatures[id].personalName);
			}
		}
		
	}


}
