package com.ccode.alchemonsters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ccode.alchemonsters.combat.ailments.StatusAilmentDictionary;
import com.ccode.alchemonsters.combat.moves.MoveDictionary;
import com.ccode.alchemonsters.creature.CreatureDictionary;
import com.ccode.alchemonsters.engine.event.EventManager;

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
		
		//Init event system
		EventManager.init();
		
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
