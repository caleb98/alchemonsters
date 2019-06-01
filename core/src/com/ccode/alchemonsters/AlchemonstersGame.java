package com.ccode.alchemonsters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ccode.alchemonsters.combat.IMoveAction;
import com.ccode.alchemonsters.combat.Move;
import com.ccode.alchemonsters.combat.MoveDictionary;
import com.ccode.alchemonsters.combat.StatusAilmentDictionary;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.CreatureDictionary;
import com.ccode.alchemonsters.creature.CreatureFactory;

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
		
		//Load dictionaries
		CreatureDictionary.initAndLoad();
		MoveDictionary.initAndLoad();
		StatusAilmentDictionary.init();
		
		//Rendering setup
		batch = new SpriteBatch();
		UI.initAndLoad();
		
		//Set initial screen
		setScreen(mainMenu);
		
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
