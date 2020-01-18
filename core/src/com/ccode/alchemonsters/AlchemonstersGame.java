package com.ccode.alchemonsters;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ccode.alchemonsters.engine.ProcessManager;
import com.ccode.alchemonsters.engine.ScriptManager;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.database.CreatureDatabase;
import com.ccode.alchemonsters.engine.database.GameData;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.database.ScriptDatabase;
import com.ccode.alchemonsters.engine.database.StatCalculators;
import com.ccode.alchemonsters.engine.database.StatusAilmentDatabase;

public class AlchemonstersGame extends Game {
	
	private static final int VIRTUAL_SCREEN_WIDTH = 640;
	private static final int VIRUTAL_SCREEN_HEIGHT = 360;
	
	private boolean isExitRequested = false;
	
	//Game Process Manager
	public ProcessManager processManager;
	
	//Rendering
	public SpriteBatch batch;
	public ExtendViewport graphicsView;
	public OrthographicCamera graphicsCamera;
	public ScreenViewport uiView;
	public OrthographicCamera uiCamera;
	
	//Asset Management
	public final AssetManager assetManager = new AssetManager();
	
	@Override
	public void create () {
		
		//TEST
		
		//Initialize and load the asset manager
		assetManager.load("sprites_packed/packed.atlas", TextureAtlas.class);
		assetManager.finishLoading();
		
		//Init the Process Manager
		processManager = new ProcessManager();
		
		//Connect to game database
		GameData.initAndLoad();
		
		//Init script systems
		ScriptManager.init();
		ScriptDatabase.initAndLoad();
		
		//Load databases
		CreatureDatabase.initAndLoad();
		MoveDatabase.initAndLoad();
		StatusAilmentDatabase.init();
		StatCalculators.init();
		
		//Setup Box2d
		Box2D.init();
		
		//Rendering setup
		graphicsCamera = new OrthographicCamera();
		uiCamera = new OrthographicCamera();
		graphicsView = new ExtendViewport(VIRTUAL_SCREEN_WIDTH, VIRUTAL_SCREEN_HEIGHT, graphicsCamera);
		graphicsView.apply();
		uiView = new ScreenViewport(uiCamera);
		uiView.apply();
		batch = new SpriteBatch();
		UI.initAndLoad();
		
		//Set initial screen
		//setScreen(new MainMenuScreen(this));
		setScreen(new MainMenuScreen(this));
		
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
	public void resize(int width, int height) {
		graphicsView.update(width, height);
		graphicsView.apply();
		uiView.update(width, height, true);
		uiView.apply();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		assetManager.dispose();
	}
	
	public void requestExit() {
		isExitRequested = true;
	}
	
}
