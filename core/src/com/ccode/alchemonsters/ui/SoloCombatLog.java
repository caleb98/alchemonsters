package com.ccode.alchemonsters.ui;

import java.util.LinkedList;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Subscriber;
import com.ccode.alchemonsters.engine.event.messages.MCombatAilmentApplied;
import com.ccode.alchemonsters.engine.event.messages.MCombatAilmentRemoved;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatGroundChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatHealingReceived;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatStatBuffApplied;
import com.ccode.alchemonsters.engine.event.messages.MCombatStateChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatTerrainChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatWeatherChanged;

public class SoloCombatLog extends ScrollPane implements Subscriber {

	private LinkedList<Message> messageQueue = new LinkedList<>();
	private TextArea consoleArea;
	
	public SoloCombatLog() {
		super(new TextArea("", UI.DEFAULT_SKIN));
		consoleArea = (TextArea) getActor();
		consoleArea.setDisabled(true);
		
		setScrollbarsVisible(true);   
		setFadeScrollBars(false);     
		setFlickScroll(false);        
		setForceScroll(false, true);  
		
		subscribe(MCombatDamageDealt.ID);
		subscribe(MCombatFinished.ID);
		subscribe(MCombatStarted.ID);
		subscribe(MCombatStateChanged.ID);
		subscribe(MCombatTeamActiveChanged.ID);
		subscribe(MCombatStatBuffApplied.ID);
		subscribe(MCombatAilmentApplied.ID);
		subscribe(MCombatAilmentRemoved.ID);
		subscribe(MCombatHealingReceived.ID);
		subscribe(MCombatTerrainChanged.ID);
		subscribe(MCombatWeatherChanged.ID);
		subscribe(MCombatGroundChanged.ID);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		Message m;
		while((m = messageQueue.poll()) != null) {
			 if(m instanceof MCombatDamageDealt) {
				 MCombatDamageDealt damage = (MCombatDamageDealt) m;
					String damageText;
					if(damage.isHit) {
						damageText = String.format("%s dealt %s %s damage to %s%s", 
								damage.source.personalName, 
								damage.amount, 
								damage.elementType, 
								damage.target.personalName, 
								damage.isCrit ? " (CRIT!)" : ".");
					}
					else {
						damageText = String.format("%s attacked with %s, but missed!", damage.source.personalName, damage.cause);
					}
					println(damageText);
			 }
			 else if(m instanceof MCombatStarted) {
				 println("Combat started!");
			 }
			 else if(m instanceof MCombatFinished) {
				 println("Combat completed.");
			 }
			 else if(m instanceof MCombatStateChanged) {
				 MCombatStateChanged full = (MCombatStateChanged) m;
				 switch(full.next) {
				 
				case BATTLE_PHASE_1:
					println("Entering Battle Phase 1...");
					break;
					
				case BATTLE_PHASE_2:
					println("Entering Battle Phase 2...");
					break;
					
				case END_PHASE:
					println("Entering End Phase...");
					break;
					
				case MAIN_PHASE_1:
					println("Entering Main Phase 1...");
					break;
					
				case MAIN_PHASE_2:
					Creature doubleAttacker = full.context.variables.getAs(Creature.class, "_PREV_DOUBLE_ATTACK_MON");
					println(String.format("%s was fast enough for a second attack!", doubleAttacker.personalName));
					println("Entering Main Phase 2...");
					break;
					
				case ACTIVE_DEATH_SWAP:
					println("Entering Death Swap Phase...");
					break;
				 
				 }
			 }
			 else if(m instanceof MCombatTeamActiveChanged) {
				 MCombatTeamActiveChanged full = (MCombatTeamActiveChanged) m;
				 println(String.format("Switched from %s to %s.", 
						 full.team.get(full.prevActive).personalName, 
						 full.team.get(full.nextActive).personalName));
			 }
			 else if(m instanceof MCombatStatBuffApplied) {
				 MCombatStatBuffApplied full = (MCombatStatBuffApplied) m;
				 println(String.format("%s buffed %s's %s stat by %s.", 
						 full.source.personalName,
						 full.target.personalName,
						 full.statBuffed,
						 full.buffAmt));
			 }
			 else if(m instanceof MCombatAilmentApplied) {
				 MCombatAilmentApplied full = (MCombatAilmentApplied) m;
				 println(String.format("%s's %s applied %s to %s.", 
						 full.source.personalName,
						 full.cause,
						 full.ailmentName,
						 full.target.personalName));
			 }
			 else if(m instanceof MCombatAilmentRemoved) {
				 MCombatAilmentRemoved full = (MCombatAilmentRemoved) m;
				 println(String.format("%s's %s (caused by %s) was removed from %s.",
						 full.source.personalName,
						 full.ailmentName,
						 full.cause,
						 full.target.personalName));
			 }
			 else if(m instanceof MCombatHealingReceived) {
				 MCombatHealingReceived full = (MCombatHealingReceived) m;
				 println(String.format("%s's %s healed %s for %s.", 
						 full.source.personalName,
						 full.cause,
						 full.target.personalName,
						 full.amount));
			 }
			 else if(m instanceof MCombatTerrainChanged) {
				 MCombatTerrainChanged full = (MCombatTerrainChanged) m;
				 println(String.format("%s's %s changed the terrain from %s to %s.", 
						 full.source.personalName,
						 full.cause,
						 full.oldTerrain,
						 full.newTerrain));
			 }
			 else if(m instanceof MCombatGroundChanged) {
				 MCombatGroundChanged full = (MCombatGroundChanged) m;
				 println(String.format("%s's %s changed the ground from %s to %s.", 
						 full.source.personalName,
						 full.cause,
						 full.oldGround,
						 full.newGround));
			 }
			 else if(m instanceof MCombatWeatherChanged) {
				 MCombatWeatherChanged full = (MCombatWeatherChanged) m;
				 println(String.format("%s's %s changed the weather from %s to %s.", 
						 full.source.personalName,
						 full.cause,
						 full.oldWeather,
						 full.newWeather));
			 }
		}
	}
	
	private void println(String s) {
		consoleArea.setText(s + "\n" + consoleArea.getText());
		consoleArea.setPrefRows(consoleArea.getLines());
		layout();
	}

	@Override
	public void handleMessage(Message currentMessage) {
		messageQueue.add(currentMessage);
	}
	
	public void clear() {
		consoleArea.setText("");
		consoleArea.setPrefRows(consoleArea.getLines());
		layout();
	}
	
}
