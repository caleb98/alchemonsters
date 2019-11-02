package com.ccode.alchemonsters;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
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
import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleAction.BattleActionType;
import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleController;
import com.ccode.alchemonsters.combat.BattleTeam;
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
import com.ccode.alchemonsters.ui.SoloCombatLog;
import com.ccode.alchemonsters.ui.TeamBuilderWindow;
import com.ccode.alchemonsters.ui.TeamSoloCombatDisplay;
import com.ccode.alchemonsters.util.GameRandom;

public class TestSoloCombatScreen extends GameScreen implements InputProcessor, Screen, Publisher {
	
	//Message listener
	private ListSubscriber sub;
	
	//Overall Frame
	private TeamBuilderWindow teamABuilder;
	private TeamBuilderWindow teamBBuilder;
	
	//Combat window UI
	private Window combatWindow;
	private TeamSoloCombatDisplay teamADisplay;
	private TeamSoloCombatDisplay teamBDisplay;
	private SoloCombatLog combatTextDisplay;
	
	//Scene2d ui
	private Stage ui;
	private Table table;
	
	//Combat event and state variables
	private BattleContext battleContext;
	private boolean isCombat = false;
	
	private BattleController teamAControl;
	private CreatureTeam teamA;
	private BattleTeam battleTeamA;
	
	private BattleController teamBControl;
	private CreatureTeam teamB;
	private BattleTeam battleTeamB;
	
	private ArrayList<DelayedMoveInfo> delayedMoves = new ArrayList<>();
	
	private boolean isWaitingOnActionSelect = false;
	private boolean isWaitingOnDeathSwitchSelect = false;
	private boolean isTeamADoubleAttack = false;
	private boolean isTeamBDoubleAttack = false;
	
	public TestSoloCombatScreen(AlchemonstersGame game) {
		super(game);
	}
	
	@Override
	public void show() {
		teamA = new CreatureTeam();
		battleTeamA = new BattleTeam(teamA, 1);
		teamB = new CreatureTeam();
		battleTeamB = new BattleTeam(teamB, 1);
		
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
		
		Window menuWindow = new Window("Options", UI.DEFAULT_SKIN);
		menuWindow.center();
		
		TextButton startCombatButton = new TextButton("Start Combat", UI.DEFAULT_SKIN);
		startCombatButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(battleTeamA.active(0) == null) {
					boolean hasAvailableActive = false;
					for(int i = 1; i < battleTeamA.creatures().length; ++i) {
						if(battleTeamA.get(i) != null) {
							battleTeamA.setActive(0, i);
							hasAvailableActive = true;
							break;
						}
					}
					if(!hasAvailableActive) {
						errorMessage.show(ui);
						return;
					}
				}
				if(battleTeamB.active(0) == null) {
					boolean hasAvailableActive = false;
					for(int i = 1; i < battleTeamB.creatures().length; ++i) {
						if(battleTeamB.get(i) != null) {
							battleTeamB.setActive(0, i);
							hasAvailableActive = true;
							break;
						}
					}
					if(!hasAvailableActive) {
						errorMessage.show(ui);
						return;
					}
				}
				startCombat();
			}
		});
		menuWindow.add(startCombatButton);
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
		
		//COMBAT WINDOW SETUP
		combatWindow = new Window("Combat Display", UI.DEFAULT_SKIN);
		combatWindow.setMovable(false);
		
		teamADisplay = new TeamSoloCombatDisplay("Team A", battleTeamA);
		teamAControl = teamADisplay;
		teamBDisplay = new TeamSoloCombatDisplay("Team B", battleTeamB);
		teamBControl = teamBDisplay;
		
		combatTextDisplay = new SoloCombatLog();
		
		combatWindow.add(teamADisplay).expand().fillY();
		combatWindow.add(combatTextDisplay).expand().fill().minWidth(400);
		combatWindow.add(teamBDisplay).expand().fillY();
		
		table.add(combatWindow).expand().fill();
		
		InputMultiplexer multi = new InputMultiplexer(ui, this);
		Gdx.input.setInputProcessor(multi);
		
		sub = new ListSubscriber();
		sub.subscribe(MCombatStateChanged.ID);
	}	

	private void startCombat() {
		//Combat setup
		battleContext = new BattleContext(teamAControl, battleTeamA, teamBControl, battleTeamB);
		
		battleTeamA.startCombat();
		battleTeamB.startCombat();
		
		battleContext.teamA = battleTeamA;
		battleContext.teamB = battleTeamB;
		
		isCombat = true;
		
		//Reset health and mana values
		for(Creature c : battleTeamA.creatures()) {
			if(c != null) c.rest();
		}
		for(Creature c : battleTeamB.creatures()) {
			if(c != null) c.rest();
		}
		
		//Reset to default battleground values
		battleContext.battleground.ground = GroundType.NORMAL;
		battleContext.battleground.terrain = TerrainType.NORMAL;
		battleContext.battleground.weather = WeatherType.NORMAL;
		
		combatTextDisplay.clear();
		publish(new MCombatStarted(battleContext, battleTeamA, battleTeamB));
		setCombatState(CombatState.MAIN_PHASE_1);
	}
	
	private void setCombatState(CombatState next) {
		MCombatStateChanged message = new MCombatStateChanged(battleContext, battleContext.currentState, next);
		battleContext.currentState = next;
		publish(message);
	}
	
	private void doBattleAction(BattleController control, BattleTeam team, BattleTeam other) {
		if(team.active(0).isDead() && control.getSelectedAction().type != BattleActionType.SWITCH) {
			return;
		}
		
		BattleAction action = control.getSelectedAction();
		switch(action.type) {
		
		case MOVE:
			String moveName = team.active(0).moves[action.id];
			Move move = MoveDatabase.getMove(moveName);
			team.active(0).currentMana -= move.manaCost;
			switch(move.turnType) {
			
			case CHARGE:
				//First check for dreamscape
				if(battleContext.battleground.weather == WeatherType.DREAMSCAPE) {
					publish(new MCombatChargeStarted(battleContext, team.active(0), other.active(0), move));
					for(MoveAction a : move.actions) {
						a.activate(move, battleContext, team.active(0), team, other.active(0), other);
					}
					team.active(0).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(battleContext, team.active(0), other.active(0), move));
				}
				else if(!control.isCharging()) {
					control.setCharging(action.id);
					publish(new MCombatChargeStarted(battleContext, team.active(0), other.active(0), move));
				}
				else {
					control.stopCharging();
					for(MoveAction a : move.actions) {
						a.activate(move, battleContext, team.active(0), team, other.active(0), other);
					}
					team.active(0).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(battleContext, team.active(0), other.active(0), move));
				}
				break;
				
			case DELAYED:
				delayedMoves.add(new DelayedMoveInfo(team, team.active(0), move, other, move.delayAmount));
				break;
				
			case RECHARGE:
				//Deluge makes it so that recharge abilities dont require a recharge time,
				//so only apply the recharge if the current weather is NOT deluge
				if(battleContext.battleground.weather != WeatherType.DELUGE) {
					control.setRecharging(true);	
				}
			case INSTANT:
				for(MoveAction a : move.actions) {
					a.activate(move, battleContext, team.active(0), team, other.active(0), other);
				}
				team.active(0).variables.setVariable("_PREVIOUS_MOVE", move);
				break;
			
			}
			break;
			
		case SWITCH:
			MCombatTeamActiveChanged message = new MCombatTeamActiveChanged(battleContext, control, team, team.activeId(0), action.id);
			team.setActive(0, action.id);
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
						doBattleAction(teamAControl, battleTeamA, battleTeamB);
					}
					else if(isTeamBDoubleAttack) {
						doBattleAction(teamBControl, battleTeamB, battleTeamA);
					}
					if(battleTeamA.isDefeated()) {
						publish(new MCombatFinished(battleContext, battleTeamB, battleTeamA));
						battleContext.endCombat();
						isCombat = false;
						return;
					}
					else if(battleTeamB.isDefeated()) {
						publish(new MCombatFinished(battleContext, battleTeamA, battleTeamB));
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
						
						for(ElementType t : battleTeamA.active(0).base.types) {
							if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
								battleTeamA.active(0).currentHealth -= battleTeamA.active(0).maxHealth / 16f;
								break;
							}
						}
						
						for(ElementType t : battleTeamB.active(0).base.types) {
							if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
								battleTeamB.active(0).currentHealth -= battleTeamB.active(0).maxHealth / 16f;
								break;
							}
						}
						
						//Check for sandstorm death
						if(areActivesDead()) {
							if(battleTeamA.isDefeated()) {
								publish(new MCombatFinished(battleContext, battleTeamB, battleTeamA));
								battleContext.endCombat();
								isCombat = false;
								break;
							}
							else if(battleTeamB.isDefeated()) {
								publish(new MCombatFinished(battleContext, battleTeamA, battleTeamB));
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
						
						battleTeamA.active(0).currentHealth += battleTeamA.active(0).maxHealth / 16f;
						if(battleTeamA.active(0).currentHealth > battleTeamA.active(0).maxHealth) 
							battleTeamA.active(0).currentHealth = battleTeamA.active(0).maxHealth;
						
						battleTeamB.active(0).currentHealth += battleTeamB.active(0).maxHealth / 16f;
						if(battleTeamB.active(0).currentHealth > battleTeamB.active(0).maxHealth) 
							battleTeamB.active(0).currentHealth = battleTeamB.active(0).maxHealth;
						
					}
					
					//Apply healing from tempest if applicable
					if(battleContext.battleground.weather == WeatherType.TEMPEST) {
						
						for(ElementType t : battleTeamA.active(0).base.types) {
							if(t == ElementType.UNDEAD) {
								battleTeamA.active(0).currentHealth += battleTeamA.active(0).maxHealth / 16f;
								if(battleTeamA.active(0).currentHealth > battleTeamA.active(0).maxHealth) 
									battleTeamA.active(0).currentHealth = battleTeamA.active(0).maxHealth;
								break;
							}
						}
						
						for(ElementType t : battleTeamB.active(0).base.types) {
							if(t == ElementType.UNDEAD) {
								battleTeamB.active(0).currentHealth += battleTeamB.active(0).maxHealth / 16f;
								if(battleTeamB.active(0).currentHealth > battleTeamB.active(0).maxHealth) 
									battleTeamB.active(0).currentHealth = battleTeamB.active(0).maxHealth;
								break;
							}
						}
						
					}
					
					isWaitingOnActionSelect = true;
					setDefaultControllerActions(teamAControl, battleTeamA);
					setDefaultControllerActions(teamBControl, battleTeamB);
					teamAControl.refresh();
					teamBControl.refresh();
					break;
					
				case MAIN_PHASE_2:
					//TODO: main phase if available
					break;
					
				case ACTIVE_DEATH_SWAP:
					isWaitingOnDeathSwitchSelect = true;
					
					if(battleTeamA.active(0).isDead()) {
						setDefaultControllerActions(teamAControl, battleTeamA);
						teamAControl.refresh();
						teamAControl.filterActions((a)->{return a.type != BattleActionType.SWITCH;});
					}
					
					if(battleTeamB.active(0).isDead()) {
						setDefaultControllerActions(teamBControl, battleTeamB);
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
				if(battleTeamA.active(0).isDead()) {
					doBattleAction(teamAControl, battleTeamA, battleTeamB);
				}
				if(battleTeamB.active(0).isDead()) {
					doBattleAction(teamBControl, battleTeamB, battleTeamA);
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
		
		if(battleTeamA.active(0).isDead()) {
			activesDead = true;
		}
		if(battleTeamB.active(0).isDead()) {
			activesDead = true;
		}
		
		return activesDead;
	}
	
	private void setDefaultControllerActions(BattleController control, BattleTeam team) {		
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
		
		for(int i = 0; i < team.creatures().length; ++i) {
			if(i != team.activeId(0) && team.get(i) != null && !team.get(i).isDead()) {
				actions.add(new BattleAction(BattleActionType.SWITCH, i));
			}
		}
		for(int i = 0; i < team.active(0).moves.length; ++i) {
			Move move = MoveDatabase.getMove(team.active(0).moves[i]);
			if(team.active(0).currentMana >= move.manaCost) {
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
			Move teamAMove = MoveDatabase.getMove(battleTeamA.active(0).moves[teamAAction.id]);
			Move teamBMove = MoveDatabase.getMove(battleTeamB.active(0).moves[teamBAction.id]);
			
			int teamAOrder = teamAMove.priority;
			int teamBOrder = teamBMove.priority;
			
			if(teamAOrder == teamBOrder) {
				teamAOrder = battleTeamA.active(0).calcTotalSpeed();
				teamBOrder = battleTeamB.active(0).calcTotalSpeed();
			}
			
			if(teamAOrder == teamBOrder) {
				int rand = GameRandom.nextBoolean() ? 1 : -1;
				teamAOrder = rand;
				teamBOrder = -rand;
			}
			
			if(teamAOrder > teamBOrder) {
				doBattleAction(teamAControl, battleTeamA, battleTeamB);
				doBattleAction(teamBControl, battleTeamB, battleTeamA);
			}
			else {
				doBattleAction(teamBControl, battleTeamB, battleTeamA);
				doBattleAction(teamAControl, battleTeamA, battleTeamB);
			}
			
		}
		else if(teamAAction.compareTo(teamBAction) < 0) {
			doBattleAction(teamAControl, battleTeamA, battleTeamB);
			doBattleAction(teamBControl, battleTeamB, battleTeamA);
		}
		else {
			doBattleAction(teamBControl, battleTeamB, battleTeamA);
			doBattleAction(teamAControl, battleTeamA, battleTeamB);
		}
		
		//Do delayed moves
		Iterator<DelayedMoveInfo> delays = delayedMoves.iterator();
		while(delays.hasNext()) {
			DelayedMoveInfo inf = delays.next();
			if(inf.delayTurns == 0) {
				Move move = inf.move;
				for(MoveAction a : move.actions) {
					a.activate(move, battleContext, inf.sourceCreature, inf.sourceTeam, inf.targetTeam.active(0), inf.targetTeam);
				}
				inf.sourceCreature.variables.setVariable("_PREVIOUS_MOVE", move);
				delays.remove();
			}
			else {
				inf.delayTurns--;
			}
		}
		
		if(battleTeamA.isDefeated()) {
			publish(new MCombatFinished(battleContext, battleTeamB, battleTeamA));
			battleContext.endCombat();
			isCombat = false;
			return;
		}
		else if(battleTeamB.isDefeated()) {
			publish(new MCombatFinished(battleContext, battleTeamA, battleTeamB));
			battleContext.endCombat();
			isCombat = false;
			return;
		}
		
		//check for speed double attack
		if(teamAAction.type == BattleActionType.MOVE &&
		   battleTeamA.active(0).calcTotalSpeed() >= 2 * battleTeamB.active(0).calcTotalSpeed()) {
			
			isWaitingOnActionSelect = true;
			//TODO: filter actions that cant be used for second attack?
			setDefaultControllerActions(teamAControl, battleTeamA);
			teamAControl.refresh();
			isTeamADoubleAttack = true;
			setCombatState(CombatState.MAIN_PHASE_2);
			
		}
		else if(teamBAction.type == BattleActionType.MOVE &&
				battleTeamB.active(0).calcTotalSpeed() >= 2 * battleTeamA.active(0).calcTotalSpeed()) {
			
			isWaitingOnActionSelect = true;
			//TODO: filter actions that cant be used for second attack?
			setDefaultControllerActions(teamBControl, battleTeamB);
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
	
	private class DelayedMoveInfo {
		
		BattleTeam sourceTeam;
		Creature sourceCreature;
		Move move;
		BattleTeam targetTeam;
		int delayTurns;
		
		public DelayedMoveInfo(BattleTeam team, Creature creature, Move move, BattleTeam target, int turns) {
			sourceTeam = team;
			sourceCreature = creature;
			this.move = move;
			targetTeam = target;
			delayTurns = turns;
		}
		
	}


}
