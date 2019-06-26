package com.ccode.alchemonsters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ccode.alchemonsters.combat.MoveDictionary;
import com.ccode.alchemonsters.combat.StatusAilmentDictionary;
import com.ccode.alchemonsters.creature.CreatureDictionary;
import com.ccode.alchemonsters.event.EventMessagingSystem;
import com.ccode.alchemonsters.event.Message;
import com.ccode.alchemonsters.event.MessageCallback;

public class AlchemonstersGame extends Game {
	
	//Game screens
	public final MainMenuScreen mainMenu;
	private boolean isExitRequested = false;
	
	//Rendering
	public SpriteBatch batch;
	
	public AlchemonstersGame() {
		
		//Initialize Game Screens
		mainMenu = new MainMenuScreen(this);
		
	}
	
	@Override
	public void create () {
		
		//Initialize Event Messaging
		EventMessagingSystem.init();
		
		//Load dictionaries
		CreatureDictionary.initAndLoad();
		MoveDictionary.initAndLoad();
		StatusAilmentDictionary.init();
		
		//Rendering setup
		batch = new SpriteBatch();
		UI.initAndLoad();
		
		//Set initial screen
		//setScreen(mainMenu);
		setScreen(new TestCombatScreen(this));
		
	}
	
	@Override
	public void render() {
		if(isExitRequested) {
			
			setScreen(null);
			dispose();
			
			if(UI.isInitialized()) {
				UI.dispose();
			}
			
			System.exit(0);
			
		}
		
		EventMessagingSystem.update(Gdx.graphics.getDeltaTime());
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
	
	public void requestExit() {
		isExitRequested = true;
	}
	
}
