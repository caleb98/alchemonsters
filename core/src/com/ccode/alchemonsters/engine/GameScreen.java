package com.ccode.alchemonsters.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.ccode.alchemonsters.AlchemonstersGame;

public abstract class GameScreen implements Screen {

	public AlchemonstersGame game;
	
	public GameScreen(AlchemonstersGame game) {
		this.game = game;
	}
	
	@Override
	public abstract void show();

	@Override
	public final void render(float delta) {
		
		//Clear gl buffers
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Render graphics
		game.graphicsView.apply();
		renderGraphics(delta);
		
		//Render ui
		game.uiView.apply(true);
		renderUI(delta);
		
	}
	
	public abstract void renderGraphics(float delta);
	public abstract void renderUI(float delta);

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}
	
}
