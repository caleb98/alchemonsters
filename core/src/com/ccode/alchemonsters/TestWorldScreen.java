package com.ccode.alchemonsters;

import java.util.Stack;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ccode.alchemonsters.engine.GameScreen;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.entity.BodyComponent;
import com.ccode.alchemonsters.entity.CollisionComponent;
import com.ccode.alchemonsters.entity.CollisionSystem;
import com.ccode.alchemonsters.entity.Mappers;
import com.ccode.alchemonsters.entity.ObjectTypeComponent;
import com.ccode.alchemonsters.entity.ObjectTypeComponent.ObjectType;
import com.ccode.alchemonsters.entity.TextureComponent;
import com.ccode.alchemonsters.entity.TransformComponent;
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
		
		if(followCamera) {
			game.graphicsCamera.position.set(pBody.getPosition(), 0);
			correctCamera();
		}
		
		if((fpsTimer -= delta) < 0) {
			fps = (float) (1.0 / Gdx.graphics.getDeltaTime());
			fpsTimer += fpsTime;
		}
		
		game.graphicsCamera.update();
		activeInstance.entityEngine.update(delta);
		
		if(renderDebug) {			
			collisionDebug.render(activeInstance.boxWorld, game.graphicsCamera.combined);
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

	public void removeEntity(Entity e) {
		if(Mappers.bodyComponent.has(e)) {
			BodyComponent eBody = Mappers.bodyComponent.get(e);
			activeInstance.boxWorld.destroyBody(eBody.body);
		}
		activeInstance.entityEngine.removeEntity(e);
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
		Entity bullet = new Entity();
		
		bullet.add(new TextureComponent(game.assetManager.get("sprites_packed/packed.atlas", TextureAtlas.class).findRegion("bullet"), 0, 4));
		
		//Calculate world click position
		Vector2 clickPos = new Vector2(screenX, screenY);
		game.graphicsView.unproject(clickPos);
		
		Vector2 aim = clickPos.cpy().sub(activeInstance.playerBody.getPosition()).nor();
		Vector2 impulse = aim.cpy().scl(250);
		Vector2 spawn = activeInstance.playerBody.getPosition().cpy();
		
		BodyDef bulletBodyDef = new BodyDef();
		bulletBodyDef.type = BodyType.DynamicBody;
		bulletBodyDef.position.set(spawn);
		bulletBodyDef.angle = aim.angleRad();
		
		Body bulletBody = activeInstance.boxWorld.createBody(bulletBodyDef);
		CircleShape bulletShape = new CircleShape();
		bulletShape.setRadius(4);
		
		FixtureDef bulletFixture = new FixtureDef();
		bulletFixture.shape = bulletShape;
		bulletFixture.filter.groupIndex = CollisionSystem.GROUP_WORLD_OBJECT;
		bulletBody.createFixture(bulletFixture);
		bulletShape.dispose();
		bulletBody.setLinearDamping(0.0f);
		
		bullet.add(new BodyComponent(bulletBody));
		bullet.add(new TransformComponent());
		bullet.add(new CollisionComponent());
		bullet.add(new ObjectTypeComponent(ObjectType.PROJECTILE));
		
		activeInstance.entityEngine.addEntity(bullet);		
		
		bulletBody.setUserData(bullet);
		bulletBody.applyLinearImpulse(impulse, bulletBody.getWorldCenter(), true);
		
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
			//Set current pos and unproject
			current.set(-screenX, -screenY, 0);
			game.graphicsView.unproject(current);
			current.sub(game.graphicsCamera.position.x, game.graphicsCamera.position.y, 0);
			//If the touch is down
			if(!(last.x == -1 && last.y == -1 && last.z == -1)) {
				delta.set(current.x - last.x, current.y - last.y, 0);
				game.graphicsCamera.translate(delta);
			}
			//Set prev values for next drag call
			last.set(current);
			
			correctCamera();
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouse.set(screenX, screenY);
		mouseWorld.set(screenX, screenY, 0);
		game.graphicsCamera.unproject(mouseWorld);
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
		Rectangle cameraBounds = new Rectangle(game.graphicsCamera.position.x - game.graphicsCamera.viewportWidth / 2, 
											   game.graphicsCamera.position.y - game.graphicsCamera.viewportHeight / 2, 
											   game.graphicsCamera.viewportWidth, 
											   game.graphicsCamera.viewportHeight);
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
			
			game.graphicsCamera.translate(tx, ty);
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
				
				//Check for a spawn
				MapLayer spawnLayer = activeInstance.map.getLayers().get("spawn");
				if(spawnLayer != null) {
					for(MapObject o : spawnLayer.getObjects()) {
						
						//Look through all spawn points and look for a spawn with the
						//given spawnId.
						if(o instanceof RectangleMapObject && 
						   o.getProperties().containsKey("id") &&
						   o.getProperties().get("id").equals(spawnId)) {
						
							//Cache user data (Entity) and delete old body
							Object pEntity = pBody.getUserData();
							activeInstance.boxWorld.destroyBody(pBody);
							
							//Get spawn pos
							Rectangle spawnRect = ((RectangleMapObject) o).getRectangle();
							Vector2 spawnPos = new Vector2(spawnRect.x, spawnRect.y);
							
							//Create new body def
							BodyDef pBodyDef = new BodyDef();
							pBodyDef.type = BodyType.DynamicBody;
							pBodyDef.position.set(spawnPos);
							
							//Create body
							pBody = activeInstance.boxWorld.createBody(pBodyDef);
							CircleShape pShape = new CircleShape();
							pShape.setRadius(16);
							pBody.createFixture(pShape, 0.0f);
							pShape.dispose();
							
							activeInstance.playerBody = pBody;
							
							pBody.setUserData(pEntity);
							
						}
						
					}
				}
				else {
					//TODO: error, no spawn layer
				}
				
				if(printInstanceStack) printInstanceStack();
				return;
			}
		}
		
		//Otherwise, load the new instance
		MapInstance newInstance = MapInstanceLoader.loadMapInstance(game, this, mapName, spawnId);
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
