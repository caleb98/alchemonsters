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
import com.ccode.alchemonsters.ui.TeamCombatDisplay;
import com.ccode.alchemonsters.util.GameRandom;
import com.ccode.alchemonsters.util.Triple;

public class TestCombatScreen extends GameScreen implements InputProcessor, Screen, Publisher {
	
	//Message listener
	private ListSubscriber sub;
	
	//Overall Frame
	private TeamBuilderWindow teamABuilder;
	private TeamBuilderWindow teamBBuilder;
	
	//Combat window UI
	private Window combatWindow;
	private TeamCombatDisplay teamADisplay;
	private TeamCombatDisplay teamBDisplay;
	private SoloCombatLog combatTextDisplay;
	
	//Scene2d ui
	private Stage ui;
	private Table table;
	
	//Combat event and state variables
	private BattleContext battleContext;
	private boolean isCombat = false;
	
	private CreatureTeam teamA;
	private BattleTeam battleTeamA;
	
	private CreatureTeam teamB;
	private BattleTeam battleTeamB;
	
	private ArrayList<DelayedMoveInfo> delayedMoves = new ArrayList<>();
	
	private boolean isWaitingOnActionSelect = false;
	private boolean isWaitingOnDeathSwitchSelect = false;
	
	private boolean isTeamADoubleAttack = false;
	private boolean isTeamBDoubleAttack = false;
	private int doubleAttackPosition = -1;
	
	public TestCombatScreen(AlchemonstersGame game) {
		super(game);
	}
	
	@Override
	public void show() {
		teamA = new CreatureTeam();
		battleTeamA = new BattleTeam(teamA, 2);
		teamB = new CreatureTeam();
		battleTeamB = new BattleTeam(teamB, 2);
		
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
		
		TextButton startCombatButton = new TextButton("Start 1v1", UI.DEFAULT_SKIN);
		startCombatButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(battleTeamA.active(0) == null) {
					boolean hasAvailableActive = false;
					for(int i = 0; i < battleTeamA.creatures().length; ++i) {
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
					teamADisplay.updateActives();
				}
				if(battleTeamB.active(0) == null) {
					boolean hasAvailableActive = false;
					for(int i = 0; i < battleTeamB.creatures().length; ++i) {
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
					teamBDisplay.updateActives();
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
		
		table.add(teamBuilders).expandY().fill().align(Align.topLeft).prefWidth(300);
		
		//COMBAT WINDOW SETUP
		combatWindow = new Window("Combat Display", UI.DEFAULT_SKIN);
		combatWindow.setMovable(false);
		
		teamADisplay = new TeamCombatDisplay("Team A", battleTeamA);
		teamBDisplay = new TeamCombatDisplay("Team B", battleTeamB);
		
		combatTextDisplay = new SoloCombatLog();
		
		Table teamDisplayTable = new Table();
		teamDisplayTable.add(teamADisplay).expand().fill().left();
		teamDisplayTable.add(teamBDisplay).expand().fill().right();
		combatWindow.add(teamDisplayTable).expandX().fill();
		combatWindow.row();
		combatWindow.add(combatTextDisplay).expand().fill().padTop(20);
		
		table.add(combatWindow).expand().fill();
		
		InputMultiplexer multi = new InputMultiplexer(ui, this);
		Gdx.input.setInputProcessor(multi);
		
		sub = new ListSubscriber();
		sub.subscribe(MCombatStateChanged.ID);
		sub.subscribe(MCombatTeamActiveChanged.ID);
	}	

	private void startCombat() {
		//Combat setup
		battleContext = new BattleContext(teamADisplay.getControllers(), battleTeamA, teamBDisplay.getControllers(), battleTeamB);
		
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
	
	private void doBattleAction(BattleController control, int activePos, BattleTeam team, BattleTeam other) {
		if(team.active(activePos).isDead() && control.getSelectedAction().type != BattleActionType.SWITCH) {
			return;
		}
		
		BattleAction action = control.getSelectedAction();
		switch(action.type) {
		
		case MOVE:
			String moveName = team.active(activePos).moves[action.id];
			Move move = MoveDatabase.getMove(moveName);
			team.active(activePos).currentMana -= move.manaCost;
			switch(move.turnType) {
			
			case CHARGE:
				//First check for dreamscape
				if(battleContext.battleground.weather == WeatherType.DREAMSCAPE) {
					publish(new MCombatChargeStarted(battleContext, team.active(activePos), other.active(action.targetPos), move));
					for(MoveAction a : move.actions) {
						a.activate(move, battleContext, team.active(0), team, other.active(0), other);
					}
					team.active(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(battleContext, team.active(activePos), other.active(action.targetPos), move));
				}
				else if(!control.isCharging()) {
					control.setCharging(action.id, action.targetPos);
					publish(new MCombatChargeStarted(battleContext, team.active(activePos), other.active(action.targetPos), move));
				}
				else {
					control.stopCharging();
					for(MoveAction a : move.actions) {
						a.activate(move, battleContext, team.active(activePos), team, other.active(action.targetPos), other);
					}
					team.active(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(battleContext, team.active(activePos), other.active(action.targetPos), move));
				}
				break;
				
			case DELAYED:
				delayedMoves.add(new DelayedMoveInfo(team, team.active(activePos), move, other, move.delayAmount));
				break;
				
			case RECHARGE:
				//Deluge makes it so that recharge abilities dont require a recharge time,
				//so only apply the recharge if the current weather is NOT deluge
				if(battleContext.battleground.weather != WeatherType.DELUGE) {
					control.setRecharging(true);
				}
			case INSTANT:
				for(MoveAction a : move.actions) {
					a.activate(move, battleContext, team.active(activePos), team, other.active(action.targetPos), other);
				}
				team.active(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
				break;
			
			}
			break;
			
		case SWITCH:
			MCombatTeamActiveChanged message = new MCombatTeamActiveChanged(battleContext, control, team, team.activeId(activePos), action.id);
			team.setActive(activePos, action.id);
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
						doBattleAction(teamADisplay.getControllers()[doubleAttackPosition], doubleAttackPosition, battleTeamA, battleTeamB);
					}
					else if(isTeamBDoubleAttack) {
						doBattleAction(teamBDisplay.getControllers()[doubleAttackPosition], doubleAttackPosition, battleTeamB, battleTeamA);
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
					setDefaultControllerActions(teamADisplay.getControllers(), battleTeamA);
					setDefaultControllerActions(teamBDisplay.getControllers(), battleTeamB);
					for(int i = 0; i < teamADisplay.getControllers().length; ++i) {
						teamADisplay.getControllers()[i].refresh();
					}
					for(int i = 0; i < teamBDisplay.getControllers().length; ++i) {
						teamBDisplay.getControllers()[i].refresh();
					}
					break;
					
				case MAIN_PHASE_2:
					if(isTeamADoubleAttack) {
						setDefaultControllerActions(teamADisplay.getControllers()[doubleAttackPosition], doubleAttackPosition, battleTeamA);
						teamADisplay.getControllers()[doubleAttackPosition].refresh();
					}
					else if(isTeamBDoubleAttack) {
						setDefaultControllerActions(teamBDisplay.getControllers()[doubleAttackPosition], doubleAttackPosition, battleTeamB);
						teamADisplay.getControllers()[doubleAttackPosition].refresh();
					}
					isWaitingOnActionSelect = true;
					
					break;
					
				case ACTIVE_DEATH_SWAP:
					if(!isWaitingOnDeathSwitchSelect) {
						
						for(int i = 0; i < battleTeamA.getNumActives(); ++i) {
							Creature creature = battleTeamA.active(i);
							if(creature.isDead()) {
								teamADisplay.getControllers()[i].refresh();
								teamADisplay.getControllers()[i].filterActions((a)->{return a.type != BattleActionType.SWITCH;});
							}
						}
						
						for(int i = 0; i < battleTeamB.getNumActives(); ++i) {
							Creature creature = battleTeamB.active(i);
							if(creature.isDead()) {
								teamBDisplay.getControllers()[i].refresh();
								teamBDisplay.getControllers()[i].filterActions((a)->{return a.type != BattleActionType.SWITCH;});
							}
						}

						isWaitingOnDeathSwitchSelect = true;
						
					}
					break;
					
				default:
					break;
				
				}
			}
		}
		
		if(isWaitingOnActionSelect) {
			if(areTeamActionsSelected()) {
				isWaitingOnActionSelect = false;
				if(battleContext.currentState == CombatState.MAIN_PHASE_1)
					setCombatState(CombatState.BATTLE_PHASE_1);
				else if(battleContext.currentState == CombatState.MAIN_PHASE_2)
					setCombatState(CombatState.BATTLE_PHASE_2);
			}
		}
		else if(isWaitingOnDeathSwitchSelect) {
			if(areTeamActionsSelected()) {				
				for(int i = 0; i < battleTeamA.getNumActives(); ++i) {
					Creature creature = battleTeamA.active(i);
					if(creature.isDead()) {
						doBattleAction(teamADisplay.getControllers()[i], i, battleTeamA, battleTeamB);
					}
				}
				
				for(int i = 0; i < battleTeamB.getNumActives(); ++i) {
					Creature creature = battleTeamB.active(i);
					if(creature.isDead()) {
						doBattleAction(teamBDisplay.getControllers()[i], i, battleTeamB, battleTeamA);
					}
				}
				
				isWaitingOnDeathSwitchSelect = false;
				setCombatState(CombatState.MAIN_PHASE_1);
			}
		}
		
		if(isCombat) {
			battleContext.update();
		}
		
		ui.act(delta);
		ui.draw();
		
	}
	
	private boolean areTeamActionsSelected() {
		for(BattleController c : teamADisplay.getControllers()) {
			if(!c.isActionSelected()) {
				return false;
			}
		}
		for(BattleController c : teamBDisplay.getControllers()) {
			if(!c.isActionSelected()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean areActivesDead() {
		for(int i = 0; i < battleTeamA.getNumActives(); ++i) {
			if(battleTeamA.active(i).isDead()) {
				return true;
			}
		}
		for(int i = 0; i < battleTeamB.getNumActives(); ++i) {
			if(battleTeamB.active(i).isDead()) {
				return true;
			}
		}
		
		return false;
	}
	
	private void setDefaultControllerActions(BattleController[] controls, BattleTeam team) {
		for(int position = 0; position < controls.length; ++position) {
			BattleController control = controls[position];
			setDefaultControllerActions(control, position, team);
		}
		if(team == battleTeamA) {
			teamADisplay.updateStrings();
		}
		else if(team == battleTeamB) {
			teamBDisplay.updateStrings();
		}
	}
	
	private void setDefaultControllerActions(BattleController control, int position,  BattleTeam team) {
		Creature creature = team.active(position);
		ArrayList<BattleAction> actions = new ArrayList<>();
		
		//Check for special charging case - we'll need different actions
		if(control.isCharging()) {
			actions.add(new BattleAction(BattleActionType.MOVE, control.getChargingTargetPos(), control.getCharging()));
			control.setAvailableActions(actions);
			return;
		}
		
		if(control.isRecharging()) {
			//TODO: this uses -1 for target index because the "wait" action doesn't have a logical target, maybe a problem?
			actions.add(new BattleAction(BattleActionType.WAIT, -1, 0));
			control.setAvailableActions(actions);
			return;
		}
		
		for(int i = 0; i < team.creatures().length; ++i) {
			if(!team.isActive(i) && team.get(i) != null && !team.get(i).isDead()) {
				actions.add(new BattleAction(BattleActionType.SWITCH, -1, i));
			}
		}
		
		//Add moves targeting each of the enemies
		int numEnemies = (team == battleTeamA) ? battleTeamB.getNumActives() : battleTeamA.getNumActives();
		for(int tar = 0; tar < numEnemies; ++tar) {
			for(int i = 0; i < creature.moves.length; ++i) {
				Move move = MoveDatabase.getMove(creature.moves[i]);
				if(creature.currentMana >= move.manaCost) {
					actions.add(new BattleAction(BattleActionType.MOVE, tar, i));
				}
			}
			actions.add(new BattleAction(BattleActionType.WAIT, tar, 0));
		}
		
		control.setAvailableActions(actions);
	}
	
	private void doBattlePhase() {
		//Triple contains <team of unit, active position id, the action fctiveId(activePos), action.id);or that unit>
		ArrayList<Triple<BattleTeam, Integer, BattleAction>> monActions = new ArrayList<>();
		for(int i = 0; i < teamADisplay.getControllers().length; ++i) {
			monActions.add(new Triple<>(battleTeamA, i, teamADisplay.getControllers()[i].getSelectedAction()));
		}
		for(int i = 0; i < teamBDisplay.getControllers().length; ++i) {
			monActions.add(new Triple<>(battleTeamB, i, teamBDisplay.getControllers()[i].getSelectedAction()));
		}
		
		monActions.sort((a, b)->{
			return a.c.compareTo(b.c);
		});
		
		//Process all switch/use actions 
		Iterator<Triple<BattleTeam, Integer, BattleAction>> iter = monActions.iterator();
		while(iter.hasNext()) {
			Triple<BattleTeam, Integer, BattleAction> info = iter.next();
			//Run all actions until we reach the move actions.
			if(info.c.type == BattleActionType.MOVE) {
				break;
			}
			else {
				doBattleAction(info.a == battleTeamA ? teamADisplay.getControllers()[info.b] : teamBDisplay.getControllers()[info.b], 
						       info.b, 
						       info.a, 
						       info.a == battleTeamA ? battleTeamB : battleTeamA);
				iter.remove();
			}
		}
		
		//

		monActions.sort((a, b)->{
			//Get the moves
			Move aMove = MoveDatabase.getMove(a.a.active(a.b).moves[a.c.id]);
			Move bMove = MoveDatabase.getMove(b.a.active(b.b).moves[b.c.id]);
			
			//Compare by priority first
			if(aMove.priority > bMove.priority) {
				return 1;
			}
			else if(aMove.priority < bMove.priority) {
				return -1;
			}
			
			//If priorities were equal, compare by speed
			Creature aMon = a.a.active(a.b);
			Creature bMon = b.a.active(b.b);
			int aSpeed = aMon.calcTotalSpeed();
			int bSpeed = bMon.calcTotalSpeed();
			
			if(aSpeed > bSpeed) {
				return 1;
			}
			else if(aSpeed < bSpeed) {
				return -1;
			}
			
			//If speeds were equal, select randomly
			return GameRandom.nextBoolean() ? -1 : 1;
		});
		
		//Run the rest of the actions
		//This will be all the moves followed by any "wait" actions
		for(Triple<BattleTeam, Integer, BattleAction> info : monActions) {
			doBattleAction(teamADisplay.getControllers()[info.b], info.b, info.a, info.a == battleTeamA ? battleTeamB : battleTeamA);
		}
		
		//Check for double attack
		//First sort by speed
		monActions.sort((a, b)->{
			int aSpeed = a.a.active(a.b).calcTotalSpeed();
			int bSpeed = b.a.active(b.b).calcTotalSpeed();
			
			if(aSpeed != bSpeed) {
				return aSpeed - bSpeed;
			}
			else {
				return GameRandom.nextBoolean() ? -1 : 1;
			}
		});
		
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
		
		//Check for defeat
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
		
		if(monActions.size() == 1) {
			//Only one mon did a move, but we still want the possibility for
			//a double attack. Check this mon's speed against the highest 
			//speed of the enemy team.
			Triple<BattleTeam, Integer, BattleAction> firstInfo = monActions.get(0);
			BattleTeam otherTeam = (firstInfo.a == battleTeamA) ? battleTeamB : battleTeamA;
			
			int maxSpeed = 0;
			for(int activeIndex : otherTeam.getActives()) {
				int thisSpeed = otherTeam.get(activeIndex).calcTotalSpeed();
				if(thisSpeed > maxSpeed) {
					maxSpeed = thisSpeed;
				}
			}
			
			if(firstInfo.a.get(firstInfo.b).calcTotalSpeed() >= 1.5 * maxSpeed) {
				//There should be a double attack
				if(firstInfo.a == battleTeamA) {
					isTeamADoubleAttack = true;
				}
				else {
					isTeamBDoubleAttack = false;
				}
				doubleAttackPosition = firstInfo.b;
				setCombatState(CombatState.MAIN_PHASE_2);
			}
		}
		else if(monActions.size() > 1) {
			//If there's a double attack, the fastest mon should have at least 1.5 the 
			//speed of the second mon
			Triple<BattleTeam, Integer, BattleAction> firstInfo = monActions.get(0);
			Triple<BattleTeam, Integer, BattleAction> secondInfo = monActions.get(1);
			int firstSpeed = firstInfo.a.active(firstInfo.b).calcTotalSpeed();
			int secondSpeed = secondInfo.a.active(secondInfo.b).calcTotalSpeed();
			
			if(firstSpeed >= 1.5 * secondSpeed) {
				//There should be a double attack
				if(firstInfo.a == battleTeamA) {
					isTeamADoubleAttack = true;
				}
				else {
					isTeamBDoubleAttack = false;
				}
				doubleAttackPosition = firstInfo.b;
				setCombatState(CombatState.MAIN_PHASE_2);
			}
		}
		
		setCombatState(CombatState.END_PHASE);
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
