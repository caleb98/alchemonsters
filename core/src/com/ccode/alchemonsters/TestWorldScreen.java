package com.ccode.alchemonsters;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class TestWorldScreen implements Screen, InputProcessor {
	
	private TmxMapLoader mapLoader = new TmxMapLoader();
	
	private Stack<MapInstance> instanceStack = new Stack<>();
	private MapInstance activeInstance;
	
	//Active instance references
	private Body pBody;
	private int mapWidth;
	private int mapHeight;
	private int tileWidth;
	private int tileHeight;

	private Box2DDebugRenderer collisionDebug = new Box2DDebugRenderer();
	
	//Camera control
	private ExtendViewport viewport;
	private OrthographicCamera camera;
	private Vector3 current = new Vector3();
	private Vector3 last = new Vector3(-1, -1, -1);
	private Vector3 delta = new Vector3();
	
	private boolean renderDebug = false;
	private boolean followCamera = true;
	
	//Rendering
	private ShapeRenderer shapes = new ShapeRenderer();
	private SpriteBatch batch;
	private BitmapFont font = new BitmapFont();
	private Vector3 textPos = new Vector3();
	private Texture player;
	
	//Player movement
	private float pv = 128;
	
	//Mouse tracking
	private Vector2 mouse = new Vector2();
	private Vector3 mouseWorld = new Vector3();
	
	@Override
	public void show() {
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		camera.update();
		
		viewport = new ExtendViewport(1024, 576, camera);
		
		Gdx.input.setInputProcessor(this);
	
		batch = new SpriteBatch();
		player = new Texture(Gdx.files.internal("data/sprites/player.png"));
		font.setColor(Color.YELLOW);
		
		loadRootMap("test1");
		
	}

	@Override
	public void render(float delta) {
		
		float vy = 0;
		float vx = 0;
		
		//Update
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			vy += pv;
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			vy -= pv;
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			vx -= pv;
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			vx += pv;
		}
		
		pBody.setLinearVelocity(vx, vy);
		
		activeInstance.boxWorld.step(delta, 6, 2);
		
		if(followCamera) {
			camera.position.set(pBody.getPosition(), 0);
			correctCamera();
		}
		
		double fps = 1.0 / Gdx.graphics.getDeltaTime();
		
		//Draw
		Gdx.gl.glClearColor(0f, 0.0f, 0.0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		activeInstance.renderer.setView(camera);
		activeInstance.renderer.render();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();		
		batch.draw(player, pBody.getPosition().x, pBody.getPosition().y);
		
		camera.unproject(textPos.set(5f, 5f, 0f));		
		font.draw(batch, String.format("%.1f", fps), textPos.x, textPos.y);
		
		batch.end();
		
		if(renderDebug) {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapes.setProjectionMatrix(camera.combined);
			for(MapLayer l : activeInstance.map.getLayers()) {
				for(MapObject o : l.getObjects()) {
					if(o instanceof RectangleMapObject) {
						RectangleMapObject rectObj = (RectangleMapObject) o;
						Rectangle rect = rectObj.getRectangle();
						
						if(rect.contains(mouseWorld.x, mouseWorld.y)) {
							shapes.setColor(0, 0, 0.5f, 0.25f);
							shapes.begin(ShapeType.Filled);
							shapes.rect(rect.x, rect.y, rect.width, rect.height);
							shapes.end();
						}					
					}
				}
			}
			Gdx.gl.glDisable(GL20.GL_BLEND);
			
			collisionDebug.render(activeInstance.boxWorld, camera.combined);
		}
		
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
			
		case Keys.F1:
			renderDebug = !renderDebug;
			break;
			
		case Keys.F2:
			followCamera = !followCamera;
			break;
			
		case Keys.F3:
			loadStackMap("test1");
			break;
			
		case Keys.F4:
			if(instanceStack.size() > 0) {
				exitStackMap();
			}
			break;
		
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
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
		if(!followCamera) {
			camera.unproject(current.set(screenX, screenY, 0));
			if(!(last.x == -1 && last.y == -1 && last.z == -1)) {
				camera.unproject(delta.set(last.x, last.y, 0));
				delta.sub(current);
				camera.position.add(delta.x, delta.y, 0);
			}
			last.set(screenX, screenY, 0);
			
			correctCamera();
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouse.set(screenX, screenY);
		camera.unproject(mouseWorld.set(screenX, screenY, 0));
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	/**
	 * Camera methods
	 */
	private void correctCamera() {
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
	}
	
	/**
	 * World loading functions
	 */
	private void loadStackMap(String mapName) {
		loadStackMap(mapName, "main");
	}
	
	private void loadStackMap(String mapName, String spawnId) {
		instanceStack.push(activeInstance);
		setActiveInstance(loadMapInstance(mapName));
	}
	
	private void exitStackMap() {
		setActiveInstance(instanceStack.pop());
	}
	
	private void loadRootMap(String mapName) {
		//Load the instance
		setActiveInstance(loadMapInstance(mapName));
		
		//Clean the instance stack
		instanceStack.clear();
	}
	
	private void setActiveInstance(MapInstance instance) {
		activeInstance = instance;
		MapProperties props = activeInstance.map.getProperties();
		
		//Update map property values
		mapWidth = props.get("width", Integer.class);
		mapHeight = props.get("height", Integer.class);
		tileWidth = props.get("tilewidth", Integer.class);
		tileHeight = props.get("tileheight", Integer.class);
		
		//Update our reference to the player body
		pBody = activeInstance.playerBody;
	}
	
	private MapInstance loadMapInstance(String mapName) {
		return loadMapInstance(mapName, "main");
	}
	
	private MapInstance loadMapInstance(String mapName, String spawnId) {
		//Load the map
		TiledMap map = mapLoader.load(String.format("data/maps/%s.tmx", mapName));
		OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map);
		
		//Create collisions
		World boxWorld = new World(new Vector2(0, 0), true);
		
		if(map.getLayers().get("collision") != null) {
			for(MapObject o : map.getLayers().get("collision").getObjects()) {
				if(o instanceof RectangleMapObject) {
					
					RectangleMapObject col = (RectangleMapObject) o;
					Rectangle colRect = col.getRectangle();
							
					BodyDef colBodyDef = new BodyDef();
					colBodyDef.position.set(colRect.x + colRect.width / 2, colRect.y + colRect.height / 2);
					
					Body colBody = boxWorld.createBody(colBodyDef);
					PolygonShape colBox = new PolygonShape();
					colBox.setAsBox(colRect.width / 2, colRect.height / 2);
					colBody.createFixture(colBox, 0.0f);
					colBox.dispose();
					
				}
			}
		}
		
		//Add player at spawn
		Vector2 playerSpawn = new Vector2();
		boolean spawnFound = false;
		if(map.getLayers().get("spawn") != null) {
			MapLayer spawns = map.getLayers().get("spawn");
			for(MapObject spawn : spawns.getObjects()) {
				if(spawn instanceof RectangleMapObject && spawn.getProperties().containsKey("id") && spawn.getProperties().get("id").equals(spawnId)) {
					Rectangle spawnRect = ((RectangleMapObject) spawn).getRectangle();
					playerSpawn.set(spawnRect.x, spawnRect.y);
					spawnFound = true;
					break;
				}
			}
		}
		if(!spawnFound) {
			System.err.printf("[Warning] No spawn point found for map %s.\n", mapName);
		}
		
		BodyDef pBodyDef = new BodyDef();
		pBodyDef.type = BodyType.DynamicBody;
		pBodyDef.position.set(playerSpawn);
		
		Body pBody = boxWorld.createBody(pBodyDef);
		CircleShape pShape = new CircleShape();
		pShape.setRadius(16);
		pShape.setPosition(new Vector2(16, 16));
		pBody.createFixture(pShape, 0.0f);
		pShape.dispose();
		
		//Combine into map instance
		return new MapInstance(map, renderer, boxWorld, pBody);
	}
	
	private class MapInstance {
		
		TiledMap map;
		TiledMapRenderer renderer;
		World boxWorld;
		Body playerBody;
		
		MapInstance(TiledMap map, TiledMapRenderer renderer, World boxWorld, Body playerBody) {
			this.map = map;
			this.renderer = renderer;
			this.boxWorld = boxWorld;
			this.playerBody = playerBody;
		}
		
	}

}
