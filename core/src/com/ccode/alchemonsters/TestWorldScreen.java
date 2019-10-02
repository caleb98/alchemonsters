package com.ccode.alchemonsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class TestWorldScreen implements Screen, InputProcessor {
	
	private TiledMap map;
	private TiledMapRenderer renderer;
	private int mapWidth;
	private int mapHeight;
	private int tileWidth;
	private int tileHeight;
	
	private ExtendViewport viewport;
	private OrthographicCamera camera;
	private Vector3 current = new Vector3();
	private Vector3 last = new Vector3(-1, -1, -1);
	private Vector3 delta = new Vector3();
	
	private ShapeRenderer shapes = new ShapeRenderer();
	private Vector3 mouse = new Vector3();

	@Override
	public void show() {
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		camera.update();
		
		viewport = new ExtendViewport(1024, 576, camera);
		
		Gdx.input.setInputProcessor(this);
		
		map = new TmxMapLoader().load("data/maps/test.tmx");
		MapProperties props = map.getProperties();
		mapWidth = props.get("width", Integer.class);
		mapHeight = props.get("height", Integer.class);
		tileWidth = props.get("tilewidth", Integer.class);
		tileHeight = props.get("tileheight", Integer.class);
		
		renderer = new OrthogonalTiledMapRenderer(map);
		
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0f, 0.0f, 0.0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		renderer.setView(camera);
		renderer.render();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		shapes.setProjectionMatrix(camera.combined);
		for(MapLayer l : map.getLayers()) {
			for(MapObject o : l.getObjects()) {
				if(o instanceof RectangleMapObject) {
					RectangleMapObject fullObj = (RectangleMapObject) o;
					Rectangle area = fullObj.getRectangle();
					if(area.contains(mouse.x, mouse.y)) {
						
						shapes.setColor(0.5f, 0.5f, 0.8f, 0.25f);
						shapes.begin(ShapeType.Filled);
						shapes.rect(area.x, area.y, area.width, area.height);
						shapes.end();
						
						shapes.setColor(Color.BLUE);
						shapes.begin(ShapeType.Line);
						shapes.rect(area.x, area.y, area.width, area.height);
						shapes.end();
						
					}
				}
			}
		}
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
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

	/**
	 * Input processing
	 */
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode) {
		
		case Keys.F11:
			if(Gdx.graphics.isFullscreen()) {
				DisplayMode mode = Gdx.graphics.getDisplayMode();
				Gdx.graphics.setWindowedMode(mode.width, mode.height);
			}
			else {
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			}
			break;
		
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		last.set(-1, -1, -1);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		camera.unproject(current.set(screenX, screenY, 0));
		if(!(last.x == -1 && last.y == -1 && last.z == -1)) {
			camera.unproject(delta.set(last.x, last.y, 0));
			delta.sub(current);
			camera.position.add(delta.x, delta.y, 0);
		}
		last.set(screenX, screenY, 0);
		
		//Correct camera position so that it is locked within the map bounds.
		//If the map is smaller than the camera size, correct it so that none of the
		//map is ever off the camera.
		Rectangle cameraBounds = new Rectangle(camera.position.x - camera.viewportWidth / 2, 
											   camera.position.y - camera.viewportHeight / 2, 
											   camera.viewportWidth, 
											   camera.viewportHeight);
		Rectangle mapBounds = new Rectangle(0, 0, mapWidth * tileWidth, mapHeight * tileHeight);
		if(!mapBounds.contains(cameraBounds)) {
			float tx = 0;
			float ty = 0;
			
			if(cameraBounds.width > mapBounds.width) {
				if(mapBounds.x < cameraBounds.x) {
					tx = mapBounds.x - cameraBounds.x;
				}
				else if(mapBounds.width > cameraBounds.x + cameraBounds.width) {
					tx = mapBounds.width - (cameraBounds.x + cameraBounds.width);
				}
			}
			else if(cameraBounds.x < 0) { 
				tx = -cameraBounds.x;
			}
			else if(cameraBounds.x + cameraBounds.width > mapBounds.width) {
				tx = -((cameraBounds.x + cameraBounds.width) - mapBounds.width);
			}
			
			if(cameraBounds.height > mapBounds.height) {
				if(mapBounds.y < cameraBounds.y) {
					ty = mapBounds.y - cameraBounds.y;
				}
				else if(mapBounds.height > cameraBounds.y + cameraBounds.height) {
					ty = mapBounds.width - (cameraBounds.y + cameraBounds.height);
				}
			}
			else if(cameraBounds.y < 0) {
				ty = -cameraBounds.y;
			}
			else if(cameraBounds.y + cameraBounds.height > mapBounds.height) {
				ty = -((cameraBounds.y + cameraBounds.height) - mapBounds.height);
			}
			
			camera.translate(tx, ty);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		camera.unproject(mouse.set(screenX, screenY, 0));
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
