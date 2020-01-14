package com.ccode.alchemonsters;

import java.util.ArrayList;
import java.util.Comparator;
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
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.combat.TeamController;
import com.ccode.alchemonsters.combat.BiomeType;
import com.ccode.alchemonsters.combat.UnitController;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.combat.moves.MoveAction;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.GameScreen;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Publisher;
import com.ccode.alchemonsters.engine.event.Subscriber;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatStateChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;
import com.ccode.alchemonsters.ui.CombatLog;
import com.ccode.alchemonsters.ui.TeamBuilderWindow;
import com.ccode.alchemonsters.ui.TeamCombatDisplayController;
import com.ccode.alchemonsters.util.GameRandom;
import com.ccode.alchemonsters.util.Triple;

public class TestCombatScreen extends GameScreen implements Subscriber, InputProcessor, Screen, Publisher {
	
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
		subscribe(MCombatTeamActiveChanged.ID);
	}	

	private void startCombat(int positions) {
		battleTeamA = new BattleTeam(teamA, positions);
		battleTeamB = new BattleTeam(teamB, positions);
		
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
		teamADisplay.setup(battleTeamA, (new TeamController(positions)).getControls(), ui);
		teamBDisplay.setup(battleTeamB, (new TeamController(positions)).getControls(), ui);
		
		//Combat setup
		battleContext = new BattleContext(battleTeamA, battleTeamB);
		
		battleContext.teamA = battleTeamA;
		battleContext.teamB = battleTeamB;
		
		isCombat = true;
		
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
		publish(new MCombatStarted(battleContext));
		setCombatState(CombatState.MAIN_PHASE_1);
	}
	
	private void endCombat() {
		publish(new MCombatFinished(battleContext, battleTeamB, battleTeamA));
		teamADisplay.updateStrings();
		teamBDisplay.updateStrings();
		isCombat = false;
	}
	
	private void setCombatState(CombatState next) {
		MCombatStateChanged message = new MCombatStateChanged(battleContext, battleContext.currentState, next);
		battleContext.currentState = next;
		publish(message);
	}
	
	private void doBattleAction(UnitController control, int activePos, BattleTeam team, BattleTeam other) {
		if(team.get(activePos).isDead() && control.getSelectedAction().type != BattleActionType.SWITCH) {
			return;
		}
		
		BattleAction action = control.getSelectedAction();
		switch(action.type) {
		
		case MOVE:
			String moveName = team.get(activePos).moves[action.id];
			Move move = MoveDatabase.getMove(moveName);
			team.get(activePos).currentMana -= move.manaCost;
			switch(move.turnType) {
			
			case CHARGE:
				//First check for dreamscape, which makes charge moves occur instantly
				if(battleContext.battleground.weather == WeatherType.DREAMSCAPE) {
					publish(new MCombatChargeStarted(battleContext, team.get(activePos), other.get(action.targetPos), move));
					for(MoveAction a : move.actions) {
						a.activate(move, battleContext, team.get(activePos), team, other.get(action.targetPos), other);
					}
					team.get(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(battleContext, team.get(activePos), other.get(action.targetPos), move));
				}
				//If they aren't  charging already, then start the charge
				else if(!control.isCharging()) {
					control.setCharging(action.id, action.targetPos);
					publish(new MCombatChargeStarted(battleContext, team.get(activePos), other.get(action.targetPos), move));
				}
				//Otherwise they were already charging, so execute the move
				else {
					control.stopCharging();
					for(MoveAction a : move.actions) {
						a.activate(move, battleContext, team.get(activePos), team, other.get(action.targetPos), other);
					}
					team.get(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
					publish(new MCombatChargeFinished(battleContext, team.get(activePos), other.get(action.targetPos), move));
				}
				break;
				
			case DELAYED:
				delayedMoves.add(new DelayedMoveInfo(team, team.get(activePos), move, other, action.targetPos, move.delayAmount));
				break;
				
			case RECHARGE:
				//Deluge makes it so that recharge abilities dont require a recharge time,
				//so only apply the recharge if the current weather is NOT deluge
				if(battleContext.battleground.weather != WeatherType.DELUGE) {
					control.setRecharging(true);
				}
			case INSTANT:
				for(MoveAction a : move.actions) {
					a.activate(move, battleContext, team.get(activePos), team, other.get(action.targetPos), other);
				}
				team.get(activePos).variables.setVariable("_PREVIOUS_MOVE", move);
				break;
			
			}
			break;
			
		case SWITCH:
			MCombatTeamActiveChanged message = new MCombatTeamActiveChanged(battleContext, team, activePos, action.id);
			team.swap(activePos, action.id);
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
					Creature creature = battleTeamA.get(i);
					if(creature.isDead()) {
						doBattleAction(teamADisplay.getControllers()[i], i, battleTeamA, battleTeamB);
					}
				}
				
				for(int i = 0; i < battleTeamB.getNumActives(); ++i) {
					Creature creature = battleTeamB.get(i);
					if(creature.isDead()) {
						doBattleAction(teamBDisplay.getControllers()[i], i, battleTeamB, battleTeamA);
					}
				}
				
				isWaitingOnDeathSwitchSelect = false;
				teamADisplay.updateStrings();
				teamBDisplay.updateStrings();
				
				//TODO: right now, swapping after death takes us back to the main phase, but we might want to pick up somewhere else
				setCombatState(CombatState.MAIN_PHASE_1);
			}
		}
		
		ui.act(delta);
		ui.draw();
		
	}
	
	private boolean areTeamActionsSelected() {
		for(UnitController c : teamADisplay.getControllers()) {
			if(!c.isActionSubmitted()) {
				return false;
			}
		}
		for(UnitController c : teamBDisplay.getControllers()) {
			if(!c.isActionSubmitted()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkNeedsActiveSwap() {
		for(int i = 0; i < battleTeamA.getNumActives(); ++i) {
			if(battleTeamA.get(i) != null && battleTeamA.get(i).isDead()) {
				//One of the actives is dead, make sure that
				//we have a replacement possibility.
				for(int k = battleTeamA.getNumActives(); k < CreatureTeam.TEAM_SIZE; ++k) {
					if(battleTeamA.get(k) != null && !battleTeamA.get(k).isDead()) {
						return true;
					}
				}
			}
		}
		for(int i = 0; i < battleTeamB.getNumActives(); ++i) {
			if(battleTeamB.get(i) != null && battleTeamB.get(i).isDead()) {
				//One of the actives is dead, make sure that
				//we have a replacement possibility.
				for(int k = battleTeamB.getNumActives(); k < CreatureTeam.TEAM_SIZE; ++k) {
					if(battleTeamB.get(k) != null && !battleTeamB.get(k).isDead()) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private void setDefaultControllerActions(UnitController[] controls, BattleTeam team) {
		for(int position = 0; position < controls.length; ++position) {
			UnitController control = controls[position];
			setDefaultControllerActions(control, position, team);
		}
		if(team == battleTeamA) {
			teamADisplay.updateStrings();
		}
		else if(team == battleTeamB) {
			teamBDisplay.updateStrings();
		}
	}
	
	private void setDefaultControllerActions(UnitController control, int position,  BattleTeam team) {
		Creature creature = team.get(position);
		if(creature == null) {
			return;
		}
		
		ArrayList<BattleAction> actions = new ArrayList<>();
		
		if(control == null) {
			System.out.println("null!");
		}
		//Check for special charging case - we'll need different actions
		if(control.isCharging()) {
			actions.add(new BattleAction(BattleActionType.MOVE, control.getChargingTargetPos(), control.getCharging()));
			control.setAllActions(actions);
			return;
		}
		
		if(control.isRecharging()) {
			//TODO: this uses -1 for target index because the "wait" action doesn't have a logical target, maybe a problem?
			actions.add(new BattleAction(BattleActionType.WAIT, -1, 0));
			control.setAllActions(actions);
			return;
		}
		
		for(int i = team.getNumActives(); i < CreatureTeam.TEAM_SIZE; ++i) {
			if(team.get(i) != null && !team.get(i).isDead()) {
				actions.add(new BattleAction(BattleActionType.SWITCH, -1, i));
			}
		}
		
		//Add moves targeting each of the enemies
		BattleTeam enemyTeam = (team == battleTeamA) ? battleTeamB : battleTeamA;
		for(int tar = 0; tar < enemyTeam.getNumActives(); ++tar) {
			//This target is dead, so dont allow targeting it
			if(enemyTeam.get(tar) == null || enemyTeam.get(tar).isDead()) {
				continue;
			}
			for(int i = 0; i < creature.moves.length; ++i) {
				Move move = MoveDatabase.getMove(creature.moves[i]);
				if(creature.currentMana >= move.manaCost) {
					actions.add(new BattleAction(BattleActionType.MOVE, tar, i));
				}
			}
		}
		

		actions.add(new BattleAction(BattleActionType.WAIT, -1, 0));
		
		control.setAllActions(actions);
	}
	
	private void doBattlePhase() {
		//Triple contains <team of unit, active position id, the action for that unit>
		ArrayList<Triple<BattleTeam, Integer, BattleAction>> monActions = new ArrayList<>();
		for(int i = 0; i < teamADisplay.getControllers().length; ++i) {
			//Ignore dead/absent mons
			if(battleTeamA.get(i) == null || battleTeamA.get(i).isDead()) {
				continue;
			}
			monActions.add(new Triple<>(battleTeamA, i, teamADisplay.getControllers()[i].getSelectedAction()));
		}
		for(int i = 0; i < teamBDisplay.getControllers().length; ++i) {
			if(battleTeamB.get(i) == null || battleTeamB.get(i).isDead()) {
				continue;
			}
			monActions.add(new Triple<>(battleTeamB, i, teamBDisplay.getControllers()[i].getSelectedAction()));
		}
		
		//Sort by order in battle action enum
		monActions.sort((a, b)->{
			return a.c.compareTo(b.c);
		});
		
		//Process all switch, use, and wait actions 
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
		
		//Remaining actions are move
		monActions.sort((new Comparator<Triple<BattleTeam, Integer, BattleAction>>() {

			@Override
			public int compare(Triple<BattleTeam, Integer, BattleAction> a, Triple<BattleTeam, Integer, BattleAction> b) {
				//Get the moves
				Move aMove = MoveDatabase.getMove(a.a.get(a.b).moves[a.c.id]);
				Move bMove = MoveDatabase.getMove(b.a.get(b.b).moves[b.c.id]);
				
				//Compare by priority first
				if(aMove.priority > bMove.priority) {
					return 1;
				}
				else if(aMove.priority < bMove.priority) {
					return -1;
				}
				
				//If priorities were equal, compare by speed
				Creature aMon = a.a.get(a.b);
				Creature bMon = b.a.get(b.b);
				int aSpeed = aMon.calcTotalSpeed(battleContext);
				int bSpeed = bMon.calcTotalSpeed(battleContext);
				
				if(aSpeed > bSpeed) {
					return -1;
				}
				else if(aSpeed < bSpeed) {
					return 1;
				}
				
				//If speeds were equal, select randomly
				return GameRandom.nextBoolean() ? -1 : 1;
			}
			
		}));
		
		//Run the rest of the actions
		//This will be all the moves
		for(Triple<BattleTeam, Integer, BattleAction> info : monActions) {
			doBattleAction(info.a == battleTeamA ? teamADisplay.getControllers()[info.b] : teamBDisplay.getControllers()[info.b], 
				       info.b, 
				       info.a, 
				       info.a == battleTeamA ? battleTeamB : battleTeamA);
		}
		
		//Do delayed moves
		Iterator<DelayedMoveInfo> delays = delayedMoves.iterator();
		while(delays.hasNext()) {
			DelayedMoveInfo inf = delays.next();
			if(inf.delayTurns == 0) {
				Move move = inf.move;
				for(MoveAction a : move.actions) {
					a.activate(move, battleContext, inf.sourceCreature, inf.sourceTeam, inf.targetTeam.get(inf.targetPos), inf.targetTeam);
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
			endCombat();
			return;
		}
		else if(battleTeamB.isDefeated()) {
			endCombat();
			return;
		}
		
		//Check for double attack
		//First sort by speed
		monActions.sort((new Comparator<Triple<BattleTeam, Integer, BattleAction>>() {

			@Override
			public int compare(Triple<BattleTeam, Integer, BattleAction> a, Triple<BattleTeam, Integer, BattleAction> b) {
				int aSpeed = a.a.get(a.b).calcTotalSpeed(battleContext);
				int bSpeed = b.a.get(b.b).calcTotalSpeed(battleContext);
				
				if(aSpeed != bSpeed) {
					return aSpeed - bSpeed;
				}
				else {
					return GameRandom.nextBoolean() ? -1 : 1;
				}
			}
			
		}).reversed()); //Reverse the list because default sorting will be by ascending
		
		if(monActions.size() == 1) {
			//Only one mon did a move, but we still want the possibility for
			//a double attack. Check this mon's speed against the highest 
			//speed of the enemy team.
			Triple<BattleTeam, Integer, BattleAction> firstInfo = monActions.get(0);
			BattleTeam otherTeam = (firstInfo.a == battleTeamA) ? battleTeamB : battleTeamA;
			
			int maxSpeed = 0;
			for(int activeIndex = 0; activeIndex < otherTeam.getNumActives(); ++activeIndex) {
				//Ignore empty slots or dead mons
				if(otherTeam.get(activeIndex) == null || otherTeam.get(activeIndex).isDead()) {
					continue;
				}
				int thisSpeed = otherTeam.get(activeIndex).calcTotalSpeed(battleContext);
				if(thisSpeed > maxSpeed) {
					maxSpeed = thisSpeed;
				}
			}
			
			if(firstInfo.a.get(firstInfo.b).calcTotalSpeed(battleContext) >= 1.5 * maxSpeed) {
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
			int firstSpeed = firstInfo.a.get(firstInfo.b).calcTotalSpeed(battleContext);
			int secondSpeed = secondInfo.a.get(secondInfo.b).calcTotalSpeed(battleContext);
			
			if(firstSpeed >= 1.5 * secondSpeed) {
				Creature doubleAttackMon;
				//There should be a double attack
				if(firstInfo.a == battleTeamA) {
					isTeamADoubleAttack = true;
					doubleAttackMon = battleTeamA.get(firstInfo.b);
				}
				else {
					isTeamBDoubleAttack = true;
					doubleAttackMon = battleTeamB.get(firstInfo.b);
				}
				doubleAttackPosition = firstInfo.b;
				battleContext.variables.setVariable("_PREV_DOUBLE_ATTACK_MON", doubleAttackMon);
				setCombatState(CombatState.MAIN_PHASE_2);
				return;
			}
		}
		
		setCombatState(CombatState.END_PHASE);
	}
	
	@Override
	public void handleMessage(Message m) {
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
					endCombat();
					return;
				}
				else if(battleTeamB.isDefeated()) {
					endCombat();
					return;
				}
				else {
					setCombatState(CombatState.END_PHASE);	
				}
				break;
				
			case END_PHASE:
				teamADisplay.updateStrings();
				if(!checkNeedsActiveSwap()) {
					setCombatState(CombatState.MAIN_PHASE_1);
				}
				else {
					setCombatState(CombatState.ACTIVE_DEATH_SWAP);
				}
				break;
				
			case MAIN_PHASE_1:
				//Apply damage from sandstorm
				if(battleContext.battleground.weather == WeatherType.SANDSTORM) {
					
					for(int i = 0; i < battleTeamA.getNumActives(); ++i) {
						for(ElementType t : battleTeamA.get(i).base.types) {
							if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
								battleTeamA.get(i).currentHealth -= battleTeamA.get(i).maxHealth / 16f;
								break;
							}
						}
					}
					
					for(int i = 0; i < battleTeamB.getNumActives(); ++i) {
						for(ElementType t : battleTeamB.get(i).base.types) {
							if(t == ElementType.WATER || t == ElementType.FIRE || t == ElementType.FEY || t == ElementType.LIGHTNING) {
								battleTeamB.get(i).currentHealth -= battleTeamB.get(i).maxHealth / 16f;
								break;
							}
						}
					}
						
					//Check for sandstorm death
					if(checkNeedsActiveSwap()) {
						if(battleTeamA.isDefeated()) {
							endCombat();
							break;
						}
						else if(battleTeamB.isDefeated()) {
							endCombat();
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
					
					for(int i = 0; i < battleTeamA.getNumActives(); ++i) {
						battleTeamA.get(i).currentHealth += battleTeamA.get(i).maxHealth / 16f;
						if(battleTeamA.get(i).currentHealth > battleTeamA.get(i).maxHealth) 
							battleTeamA.get(i).currentHealth = battleTeamA.get(i).maxHealth;
					}
					
					for(int i = 0; i < battleTeamB.getNumActives(); ++i) {
						battleTeamB.get(i).currentHealth += battleTeamB.get(i).maxHealth / 16f;
						if(battleTeamB.get(i).currentHealth > battleTeamB.get(i).maxHealth) 
							battleTeamB.get(i).currentHealth = battleTeamB.get(i).maxHealth;
					}
					
				}
				
				//Apply healing from tempest if applicable
				if(battleContext.battleground.weather == WeatherType.TEMPEST) {
					
					for(int i = 0; i < battleTeamA.getNumActives(); ++i) {
						for(ElementType t : battleTeamA.get(i).base.types) {
							if(t == ElementType.UNDEAD) {
								battleTeamA.get(i).currentHealth += battleTeamA.get(i).maxHealth / 16f;
								if(battleTeamA.get(i).currentHealth > battleTeamA.get(i).maxHealth) 
									battleTeamA.get(i).currentHealth = battleTeamA.get(i).maxHealth;
								break;
							}
						}
					}
					
					for(int i = 0; i < battleTeamB.getNumActives(); ++i) {
						for(ElementType t : battleTeamB.get(i).base.types) {
							if(t == ElementType.UNDEAD) {
								battleTeamB.get(i).currentHealth += battleTeamB.get(i).maxHealth / 16f;
								if(battleTeamB.get(i).currentHealth > battleTeamB.get(i).maxHealth) 
									battleTeamB.get(i).currentHealth = battleTeamB.get(i).maxHealth;
								break;
							}
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
					teamBDisplay.getControllers()[doubleAttackPosition].refresh();
				}
				isWaitingOnActionSelect = true;
				break;
				
			case ACTIVE_DEATH_SWAP:
				//Trigger the following once only when we first enter this battle state
				if(!isWaitingOnDeathSwitchSelect) {
					
					for(int i = 0; i < battleTeamA.getNumActives(); ++i) {
						Creature creature = battleTeamA.get(i);
						if(creature.isDead()) {
							teamADisplay.getControllers()[i].refresh();
							teamADisplay.getControllers()[i].filterAllActions((a)->{return a.type != BattleActionType.SWITCH;});
							teamADisplay.updateStrings();
						}
					}
					
					for(int i = 0; i < battleTeamB.getNumActives(); ++i) {
						Creature creature = battleTeamB.get(i);
						if(creature.isDead()) {
							teamBDisplay.getControllers()[i].refresh();
							teamBDisplay.getControllers()[i].filterAllActions((a)->{return a.type != BattleActionType.SWITCH;});
							teamBDisplay.updateStrings();
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
		int targetPos;
		int delayTurns;
		
		public DelayedMoveInfo(BattleTeam team, Creature creature, Move move, BattleTeam target, int targetPos, int turns) {
			sourceTeam = team;
			sourceCreature = creature;
			this.move = move;
			targetTeam = target;
			this.targetPos = targetPos;
			delayTurns = turns;
		}
		
	}


}
