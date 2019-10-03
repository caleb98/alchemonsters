package com.ccode.alchemonsters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.ccode.alchemonsters.combat.ailments.StatusAilmentDictionary;
import com.ccode.alchemonsters.combat.moves.MoveDictionary;
import com.ccode.alchemonsters.creature.CreatureDictionary;
import com.ccode.alchemonsters.engine.ProcessManager;
import com.ccode.alchemonsters.engine.ScriptManager;
import com.ccode.alchemonsters.engine.event.EventManager;

public class AlchemonstersGame extends Game {
	
	private boolean isExitRequested = false;
	
	//Game Process Manager
	ProcessManager processManager;
	
	//Rendering
	public SpriteBatch batch;
	
	//Asset Management
	public final AssetManager assetManager = new AssetManager();
	
	@Override
	public void create () {
		
		//TEST
		
		//Initialize and load the asset manager
		assetManager.load("sprites_packed/packed.atlas", TextureAtlas.class);
		while(!assetManager.update()) {
			
		}
		
		//Init the Process Manager
		processManager = new ProcessManager();
		
		//Init event system
		EventManager.init();
		
		//Init script manager
		ScriptManager.init();
		
		//Load dictionaries
		CreatureDictionary.initAndLoad();
		MoveDictionary.initAndLoad();
		StatusAilmentDictionary.init();
		
		//Setup Box2d
		Box2D.init();
		
		//Rendering setup
		batch = new SpriteBatch();
		UI.initAndLoad();
		
		//Set initial screen
		//setScreen(mainMenu);
		setScreen(new TestWorldScreen(this));
		
		
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
		
		processManager.updateProcesses(Gdx.graphics.getDeltaTime());
		
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
