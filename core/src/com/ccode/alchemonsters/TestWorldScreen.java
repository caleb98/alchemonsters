package com.ccode.alchemonsters;

import java.util.Stack;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ccode.alchemonsters.engine.GameScreen;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.entity.CollisionComponent;
import com.ccode.alchemonsters.entity.Mappers;
import com.ccode.alchemonsters.world.MapInstance;
import com.ccode.alchemonsters.world.MapInstanceLoader;

public class TestWorldScreen extends GameScreen implements InputProcessor, ContactListener {
	
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
	private Vector3 current = new Vector3();
	private Vector3 last = new Vector3(-1, -1, -1);
	private Vector3 delta = new Vector3();
	
	private boolean renderDebug = false;
	private boolean followCamera = true;
	private boolean printInstanceStack = false;
	
	//Rendering
	private ShapeRenderer shapes = new ShapeRenderer();
	private Sprite player;
	
	//Player movement
	private float pv = 128;
	
	//Mouse tracking
	private Vector2 mouse = new Vector2();
	private Vector3 mouseWorld = new Vector3();
	
	//UI
	private Stage ui;
	private Table table;
	
	private Label fpsLabel;
	private TextButton menuButton;
	
	//fps display
	float fps = 0;
	float fpsTime = 0.1f;
	float fpsTimer = fpsTime;
	
	public TestWorldScreen(AlchemonstersGame game) {
		super(game);
	}
	
	@Override
	public void show() {
	
		ui = new Stage(game.uiView, game.batch);
		table = new Table();
		table.setFillParent(true);
		ui.addActor(table);
		
		fpsLabel = new Label("", UI.DEFAULT_SKIN);
		menuButton = new TextButton("Main Menu", UI.DEFAULT_SKIN);
		menuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new MainMenuScreen(game));
			}
		});
		
		table.add(fpsLabel).expandY().top();
		table.row();
		table.add(menuButton).expandY().bottom();
		table.left();
		
		InputMultiplexer input = new InputMultiplexer(ui, this);
		Gdx.input.setInputProcessor(input);
		
		player = game.assetManager.get("sprites_packed/packed.atlas", TextureAtlas.class).createSprite("player");
		
		switchToMap("city");
		
	}
	
	@Override
	public void renderGraphics(float delta) {
		
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
		
		activeInstance.entityEngine.update(delta);
		
		if(followCamera) {
			game.camera.position.set(pBody.getPosition(), 0);
			correctCamera();
		}
		
		if((fpsTimer -= delta) < 0) {
			fps = (float) (1.0 / Gdx.graphics.getDeltaTime());
			fpsTimer += fpsTime;
		}
		
		game.camera.update();
		activeInstance.renderer.setView(game.camera);
		activeInstance.renderer.render();
		
		Vector2 playerPos = pBody.getPosition();
		player.setPosition(playerPos.x, playerPos.y);
		
		if(renderDebug) {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapes.setProjectionMatrix(game.camera.combined);
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
			
			collisionDebug.render(activeInstance.boxWorld, game.camera.combined);
		}
		
	}
	
	@Override
	public void renderUI(float delta) {
		
		//Recalc fps
		if((fpsTimer -= delta) < 0) {
			fps = (float) (1.0 / Gdx.graphics.getDeltaTime());
			fpsLabel.setText(Math.round(fps));
			fpsTimer += fpsTime;
		}
		
		ui.act(delta);
		ui.draw();
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		dispose();
	}

	@Override
	public void dispose() {
		ui.dispose();
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
			printInstanceStack = !printInstanceStack;
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
			game.camera.unproject(current.set(screenX, screenY, 0));
			if(!(last.x == -1 && last.y == -1 && last.z == -1)) {
				game.camera.unproject(delta.set(last.x, last.y, 0));
				delta.sub(current);
				game.camera.position.add(delta.x, delta.y, 0);
			}
			last.set(screenX, screenY, 0);
			
			correctCamera();
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouse.set(screenX, screenY);
		game.camera.unproject(mouseWorld.set(screenX, screenY, 0));
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
		Rectangle cameraBounds = new Rectangle(game.camera.position.x - game.camera.viewportWidth / 2, 
											   game.camera.position.y - game.camera.viewportHeight / 2, 
											   game.camera.viewportWidth, 
											   game.camera.viewportHeight);
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
			
			game.camera.translate(tx, ty);
		}
	}
	
	private void printInstanceStack() {
		System.out.printf("MapInstance Stack: ");
		for(int i = 0; i < instanceStack.size(); ++i) {
			System.out.printf("%s > ", instanceStack.get(i).mapName);
		}
		System.out.printf("%s\n", activeInstance.mapName);
	}
	
	/**
	 * World loading functions
	 */
	public void switchToMap(String mapName) {
		switchToMap(mapName, "main");
	}
	
	public void switchToMap(String mapName, String spawnId) {
		//Check if the new instance is just moving back up the chain
		for(int i = instanceStack.size() - 1; i >= 0; --i) {
			if(instanceStack.get(i).mapName.equals(mapName)) {
				int popnum = instanceStack.size() - 1 - i;
				for(int p = 0; p < popnum; ++p) instanceStack.pop();
				setActiveInstance(instanceStack.pop());
				if(printInstanceStack) printInstanceStack();
				return;
			}
		}
		
		//Otherwise, load the new instance
		MapInstance newInstance = MapInstanceLoader.loadMapInstance(game, this, mapName, spawnId);
		newInstance.renderer.addSprite(player);
		Object isRootObj = newInstance.map.getProperties().get("isRoot");
		if(isRootObj != null && isRootObj instanceof Boolean) {
			boolean isRoot = (boolean) isRootObj;
			if(isRoot) {
				
				//New map is a root map
				setActiveInstance(newInstance);
				instanceStack.clear();
				
			}
			else {
			
				//New map is not a root map
				if(activeInstance != null) {
					instanceStack.push(activeInstance);
				}
				setActiveInstance(newInstance);
				
			}
		}
		else {
			//TODO: isRoot variable does not exist in the map or not not of boolean type
		}
		
		if(printInstanceStack) printInstanceStack();
		
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
	
	/**
	 * Contact listener methods for CollisionComponents
	 */
	@Override
	public void beginContact(Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();	
		
		if(a.getBody().getUserData() instanceof Entity && b.getBody().getUserData() instanceof Entity) {
			Entity ea = (Entity) a.getBody().getUserData();
			Entity eb = (Entity) b.getBody().getUserData();
			
			//Look for collision components
			CollisionComponent colA = Mappers.collisionComponent.get(ea);
			CollisionComponent colB = Mappers.collisionComponent.get(eb);
			
			//If the components were present, update the collided entity.
			if(colA != null) {
				colA.collisions.add(eb);
			}
			if(colB != null) {
				colB.collisions.add(ea);
			}
		}
	}

	@Override public void endContact(Contact contact) {}
	@Override public void preSolve(Contact contact, Manifold oldManifold) {}
	@Override public void postSolve(Contact contact, ContactImpulse impulse) {}

}
