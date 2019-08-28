package com.ccode.alchemonsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TestWorldScreen implements Screen {
	
	private TiledMap map;
	private TiledMapRenderer renderer;
	private OrthographicCamera camera;
	private OrthoCamController cControl;
	
	private Texture tile;
	

	@Override
	public void show() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, (w / h) * 640, 640);
		camera.update();
		
		cControl = new OrthoCamController(camera);
		Gdx.input.setInputProcessor(cControl);
		
		map = new TmxMapLoader().load("data/maps/test.tmx");
		
		renderer = new OrthogonalTiledMapRenderer(map);
		
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0f, 0.2f, 0.4f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		renderer.setView(camera);
		renderer.render();
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		
	}

}
