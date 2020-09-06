package com.ccode.alchemonsters.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ShortArray;
import com.ccode.alchemonsters.AlchemonstersGame;
import com.ccode.alchemonsters.TestWorldScreen;
import com.ccode.alchemonsters.entity.AnimationComponent;
import com.ccode.alchemonsters.entity.BodyComponent;
import com.ccode.alchemonsters.entity.CollisionComponent;
import com.ccode.alchemonsters.entity.CollisionSystem;
import com.ccode.alchemonsters.entity.DialogueComponent;
import com.ccode.alchemonsters.entity.DialogueSystem;
import com.ccode.alchemonsters.entity.ObjectTypeComponent;
import com.ccode.alchemonsters.entity.ObjectTypeComponent.ObjectType;
import com.ccode.alchemonsters.entity.PhysicsSystem;
import com.ccode.alchemonsters.entity.PlayerComponent;
import com.ccode.alchemonsters.entity.RenderSystem;
import com.ccode.alchemonsters.entity.TransformComponent;
import com.ccode.alchemonsters.entity.WarpComponent;
import com.kyper.yarn.Dialogue;
import com.kyper.yarn.UserData;

public class MapInstanceLoader {

	private static final TmxMapLoader MAP_LOADER = new TmxMapLoader();
	private static final EarClippingTriangulator TRIANGULATOR = new EarClippingTriangulator();
	private static final int ELLIPSE_STEPS = 32;
	
	private static final String COLLISION_LAYER_NAME = "collision";
	private static final String WARPS_LAYER_NAME = "warps";
	private static final String SPAWN_LAYER_NAME = "spawn";
	private static final String NPC_LAYER_NAME = "npc";
	
	public static MapInstance loadMapInstance(AlchemonstersGame game, TestWorldScreen world, String mapName, String spawnId) {
		//Load the map
		TiledMap map = MAP_LOADER.load(String.format("maps/%s.tmx", mapName));
		
		//Create box2d world
		World boxWorld = new World(new Vector2(0, 0), true);
		boxWorld.setContactListener(world);
		
		//Create the entity engine
		Engine entityEngine = new Engine();
		
		//Add the collision boxes to the world/entity system
		if(map.getLayers().get(COLLISION_LAYER_NAME) != null) {
			for(MapObject o : map.getLayers().get(COLLISION_LAYER_NAME).getObjects()) {
				
				createCollisionObject(o, boxWorld, entityEngine);
				
			}
		}
		
		//Add warp area boxes to the world/entity system
		if(map.getLayers().get(WARPS_LAYER_NAME) != null) {
			for(MapObject o : map.getLayers().get(WARPS_LAYER_NAME).getObjects()) {
			
				createWarpObject(o, boxWorld, entityEngine, mapName);
				
			}
		}
		
		//Add player at spawn (to box2d world/entity engine)		
		Vector2 playerSpawn = new Vector2();
		boolean spawnFound = false;
		if(map.getLayers().get(SPAWN_LAYER_NAME) != null) {
			
			MapLayer spawns = map.getLayers().get(SPAWN_LAYER_NAME);
			for(MapObject spawn : spawns.getObjects()) {
				
				if(spawn instanceof RectangleMapObject && 
				   spawn.getProperties().containsKey("id") && 
				   spawn.getProperties().get("id").equals(spawnId)) {
					
					Rectangle spawnRect = ((RectangleMapObject) spawn).getRectangle();
					playerSpawn.set(spawnRect.x, spawnRect.y);
					spawnFound = true;
					break;
					
				}
				
			}
			
		}
		if(!spawnFound) {
			System.err.printf("[Warning] No spawn point %s found for map %s.\n", spawnId, mapName);
		}
		
		//Check for npcs to spawn
		if(map.getLayers().get(NPC_LAYER_NAME) != null) {
			
			MapLayer npcs = map.getLayers().get(NPC_LAYER_NAME);
			for(MapObject npc : npcs.getObjects()) {
				
				if(npc instanceof RectangleMapObject) {
					createUnitEntity(game, (RectangleMapObject) npc, boxWorld, entityEngine);
				}
				
			}
			
		}
		
		Entity playerEntity = new Entity();
		playerEntity.add(new AnimationComponent(
			new Animation<TextureRegion>(
				1f, 
				game.assetManager.get("sprites_packed/packed.atlas", TextureAtlas.class).findRegions("player"), 
				PlayMode.LOOP), 
			16, 16
		));
		
		BodyDef pBodyDef = new BodyDef();
		pBodyDef.type = BodyType.DynamicBody;
		pBodyDef.position.set(playerSpawn);
		
		Body pBody = boxWorld.createBody(pBodyDef);
		CircleShape pShape = new CircleShape();
		pShape.setRadius(16);
		FixtureDef pFixture = new FixtureDef();
		pFixture.shape = pShape;
		pFixture.filter.groupIndex = CollisionSystem.GROUP_UNIT;
		pBody.createFixture(pFixture);
		pShape.dispose();
		
		playerEntity.add(new BodyComponent(pBody));
		playerEntity.add(new TransformComponent());
		playerEntity.add(new CollisionComponent());
		playerEntity.add(new ObjectTypeComponent(ObjectType.UNIT));
		playerEntity.add(new PlayerComponent());
		entityEngine.addEntity(playerEntity);
		
		pBody.setUserData(playerEntity);
		
		//Setup engine systems
		entityEngine.addSystem(new PhysicsSystem(boxWorld));
		entityEngine.addSystem(new CollisionSystem(world));
		entityEngine.addSystem(new DialogueSystem(playerEntity));
		entityEngine.addSystem(new RenderSystem(game, map, game.batch));
		
		//Combine into map instance
		return new MapInstance(mapName, map, boxWorld, pBody, entityEngine);
	}
	
	private static void createUnitEntity(AlchemonstersGame game, RectangleMapObject object, World boxWorld, Engine entityEngine) {
		
		MapProperties props = object.getProperties();
		
		Entity unitEntity = new Entity();
		
		//Add unit animation
		Animation<TextureRegion> anim = new Animation<>(
				1f, 
				game.assetManager.get("sprites_packed/packed.atlas", TextureAtlas.class).findRegions((String) props.get("sprite")), 
				PlayMode.LOOP
		);
		TextureRegion frame = anim.getKeyFrame(1f);
		int spriteWidth = frame.getRegionWidth();
		int spriteHeight = frame.getRegionHeight();
		unitEntity.add(new AnimationComponent(anim, spriteWidth / 2, spriteHeight / 2));
		
		//Create unit body
		BodyDef uBodyDef = new BodyDef();
		uBodyDef.type = BodyType.DynamicBody;
		uBodyDef.position.set(object.getRectangle().x, object.getRectangle().y);
		
		Body uBody = boxWorld.createBody(uBodyDef);
		CircleShape uShape = new CircleShape();
		uShape.setRadius((spriteWidth + spriteHeight) / 4); //average sprite width and height then divide by 2 (so just divide by 4 to combine average and /2)
		FixtureDef uFixture = new FixtureDef();
		uFixture.shape = uShape;
		uFixture.filter.groupIndex = CollisionSystem.GROUP_UNIT;
		uBody.createFixture(uFixture);
		uShape.dispose();
		
		CircleShape adjacentSensorShape = new CircleShape();
		adjacentSensorShape.setRadius(32 * 2f);
		FixtureDef adjacentSensorFixture = new FixtureDef();
		adjacentSensorFixture.shape = adjacentSensorShape;
		adjacentSensorFixture.isSensor = true;
		adjacentSensorFixture.filter.groupIndex = CollisionSystem.GROUP_ADJACENT_SENSOR;
		Fixture adjacentFixture = uBody.createFixture(adjacentSensorFixture);
		adjacentSensorShape.dispose();
		
		//Link entity/body
		uBody.setUserData(unitEntity);
		unitEntity.add(new BodyComponent(uBody));
		
		//Add dialogue if present
		if(props.containsKey("dialogue")) {
			//TODO: potentially use global user data variable for all dialogues?
			Dialogue dialogue = new Dialogue(new UserData(object.getName() + ":" + object.toString()));
			dialogue.loadFile("dialogue/"+ props.get("dialogue") + ".json");
			unitEntity.add(new DialogueComponent(dialogue, adjacentFixture));
		}
		
		//Add other components
		unitEntity.add(new TransformComponent());
		unitEntity.add(new CollisionComponent());
		unitEntity.add(new ObjectTypeComponent(ObjectType.UNIT));
		
		//Add the new entity to the engine
		entityEngine.addEntity(unitEntity);
		
	}
	
	private static void createCollisionObject(MapObject object, World boxWorld, Engine entityEngine) {
		
		Entity collisionEntity = new Entity();
		
		BodyDef colBodyDef = new BodyDef();		
		Body colBody = null;
		
		//Check for rotation
		float rotDeg = 0;
		float rotRad = 0;
		if(object.getProperties().containsKey("rotation")) {
			//Tiled rotation is the opposite direction of libgdx/box2d, so subtract from 360
			rotDeg = 360 - (float) object.getProperties().get("rotation");
			rotRad = (float) (rotDeg * Math.PI / 180);
		}
		colBodyDef.angle = rotRad;
		
		if(object instanceof RectangleMapObject) {
			
			RectangleMapObject collision = (RectangleMapObject) object;
			Rectangle colRect = collision.getRectangle();
			
			colBodyDef.position.set(colRect.x + colRect.width / 2, colRect.y + colRect.height / 2);
			colBody = boxWorld.createBody(colBodyDef);
			
			PolygonShape colShape = new PolygonShape();
			colShape.setAsBox(colRect.width / 2, colRect.height / 2);
			
			FixtureDef colFixture = new FixtureDef();
			colFixture.shape = colShape;
			colFixture.filter.groupIndex = CollisionSystem.GROUP_COLLISION_BOX;
			colBody.createFixture(colFixture);
			colShape.dispose();
			
		}
		else if(object instanceof EllipseMapObject) {
			
			EllipseMapObject collision = (EllipseMapObject) object;
			Ellipse colEllipse = collision.getEllipse();
			
			colBodyDef.position.set(colEllipse.x + colEllipse.width / 2, colEllipse.y + colEllipse.height / 2);
			colBody = boxWorld.createBody(colBodyDef);
			
			ChainShape colShape = new ChainShape();
			Vector2[] verts = new Vector2[ELLIPSE_STEPS];
			for(int i = 0; i < ELLIPSE_STEPS; ++i) {
				float t = i * (float) (2 * Math.PI) / ELLIPSE_STEPS;
				verts[i] = new Vector2(colEllipse.width / 2 * (float) Math.cos(t), colEllipse.height / 2 * (float) Math.sin(t));
			}
			colShape.createLoop(verts);
			FixtureDef colFixture = new FixtureDef();
			colFixture.shape = colShape;
			colFixture.filter.groupIndex = CollisionSystem.GROUP_COLLISION_BOX;
			colBody.createFixture(colFixture);
			colShape.dispose();
			
		}
		else if(object instanceof PolygonMapObject) {
			
			PolygonMapObject collision = (PolygonMapObject) object;
			Polygon colPoly = collision.getPolygon();
			
			colBodyDef.position.set(colPoly.getX(), colPoly.getY());
			colBody = boxWorld.createBody(colBodyDef);
			
			ShortArray triangles = TRIANGULATOR.computeTriangles(colPoly.getVertices());
			PolygonShape colShape = new PolygonShape();
			float[]	allVerts = colPoly.getVertices();
			
			for(int i = 0; i < triangles.size; i += 3) {
				short[] tri = { triangles.get(i), triangles.get(i+1), triangles.get(i+2) };
				float[] triPoints = {
						allVerts[2*tri[0]], allVerts[2*tri[0] + 1],
						allVerts[2*tri[1]], allVerts[2*tri[1] + 1], 
						allVerts[2*tri[2]], allVerts[2*tri[2] + 1]
				};
				
				colShape.set(triPoints);
				FixtureDef colFixture = new FixtureDef();
				colFixture.shape = colShape;
				colFixture.filter.groupIndex = CollisionSystem.GROUP_COLLISION_BOX;
				colBody.createFixture(colFixture);
			}
			
			colShape.dispose();
			
		}
		else if(object instanceof PolylineMapObject) {
			
			PolylineMapObject collision = (PolylineMapObject) object;
			Polyline colPoly = collision.getPolyline();
			
			colBodyDef.position.set(colPoly.getX(), colPoly.getY());
			colBody = boxWorld.createBody(colBodyDef);
			
			ChainShape colShape = new ChainShape();
			colShape.createChain(colPoly.getVertices());
			FixtureDef colFixture = new FixtureDef();
			colFixture.shape = colShape;
			colFixture.filter.groupIndex = CollisionSystem.GROUP_COLLISION_BOX;
			colBody.createFixture(colFixture);
			colShape.dispose();
			
		}
		else {
			
			System.out.println("[Error] Attempted to create collision object from incompatible collision shape!");
			return;
			
		}
		
		collisionEntity.add(new BodyComponent(colBody));
		collisionEntity.add(new TransformComponent());
		collisionEntity.add(new ObjectTypeComponent(ObjectType.COLLISION_BOX));
		entityEngine.addEntity(collisionEntity);
		
		colBody.setUserData(collisionEntity);
		
	}
	
	private static void createWarpObject(MapObject object, World boxWorld, Engine entityEngine, String mapName) {
		
		//Check for connection property
		MapProperties props = object.getProperties();
		if(!props.containsKey("connectedMap")) {
			System.err.printf("[Error] Unable to load warp area in %s (no connectedMap property.)\n", mapName);
			return;
		}
		String connectedMap = (String) props.get("connectedMap");
		String connectedSpawn = props.containsKey("connectedSpawn") ? (String) props.get("connectedSpawn") : "main";
		
		Entity warpEntity = new Entity();
		
		BodyDef warpBodyDef = new BodyDef();
		FixtureDef warpFixDef = new FixtureDef();
		warpFixDef.isSensor = true;
		Body warpBody;
		
		//Check for rotation
		float rotDeg = 0;
		float rotRad = 0;
		if(props.containsKey("rotation")) {
			rotDeg = 360 - (float) props.get("rotation");
			rotRad = rotDeg * (float) Math.PI / 180;
		}
		warpBodyDef.angle = rotRad;
		
		if(object instanceof RectangleMapObject) {
			
			RectangleMapObject warp = (RectangleMapObject) object;
			Rectangle warpRect = warp.getRectangle();
			
			warpBodyDef.position.set(warpRect.x + warpRect.width / 2, warpRect.y + warpRect.height / 2);
			warpBody = boxWorld.createBody(warpBodyDef);
			
			PolygonShape warpShape = new PolygonShape();
			warpShape.setAsBox(warpRect.width / 2, warpRect.height / 2);
			warpFixDef.shape = warpShape;
			warpBody.createFixture(warpFixDef);
			warpShape.dispose();
			
		}
		else if(object instanceof EllipseMapObject) {
			
			EllipseMapObject warp = (EllipseMapObject) object;
			Ellipse warpEllipse = warp.getEllipse();
			
			warpBodyDef.position.set(warpEllipse.x + warpEllipse.width / 2, warpEllipse.y + warpEllipse.height / 2);
			warpBody = boxWorld.createBody(warpBodyDef);
			
			ChainShape warpShape = new ChainShape();
			Vector2[] verts = new Vector2[ELLIPSE_STEPS];
			for(int i = 0; i < ELLIPSE_STEPS; ++i) {
				float t = i * (float) (2 * Math.PI) / ELLIPSE_STEPS;
				verts[i] = new Vector2(warpEllipse.width / 2 * (float) Math.cos(t), warpEllipse.height / 2 * (float) Math.sin(t));
			}
			warpShape.createLoop(verts);
			warpFixDef.shape = warpShape;
			warpBody.createFixture(warpFixDef);
			warpShape.dispose();
			
		}
		else if(object instanceof PolygonMapObject) {
			
			PolygonMapObject warp = (PolygonMapObject) object;
			Polygon warpPoly = warp.getPolygon();
			
			warpBodyDef.position.set(warpPoly.getX(), warpPoly.getY());
			warpBody = boxWorld.createBody(warpBodyDef);
			
			ShortArray triangles = TRIANGULATOR.computeTriangles(warpPoly.getVertices());
			PolygonShape warpShape = new PolygonShape();
			float[]	allVerts = warpPoly.getVertices();
			
			for(int i = 0; i < triangles.size; i += 3) {
				short[] tri = { triangles.get(i), triangles.get(i+1), triangles.get(i+2) };
				float[] triPoints = {
						allVerts[2*tri[0]], allVerts[2*tri[0] + 1],
						allVerts[2*tri[1]], allVerts[2*tri[1] + 1], 
						allVerts[2*tri[2]], allVerts[2*tri[2] + 1]
				};
				
				warpShape.set(triPoints);
				warpFixDef.shape = warpShape;
				warpBody.createFixture(warpFixDef);
			}
			
			warpShape.dispose();
			
		}
		else if(object instanceof PolylineMapObject) {
			
			PolylineMapObject warp = (PolylineMapObject) object;
			Polyline warpPoly = warp.getPolyline();
			
			warpBodyDef.position.set(warpPoly.getX(), warpPoly.getY());
			warpBody = boxWorld.createBody(warpBodyDef);
			
			ChainShape warpShape = new ChainShape();
			warpShape.createChain(warpPoly.getVertices());
			warpFixDef.shape = warpShape;
			warpBody.createFixture(warpFixDef);
			warpShape.dispose();
			
		}
		else {
			
			System.out.println("[Error] Attempted to create warp object from incompatible warp shape!");
			return;
			
		}
		
		warpEntity.add(new BodyComponent(warpBody));
		warpEntity.add(new TransformComponent());
		warpEntity.add(new ObjectTypeComponent(ObjectType.WARP_AREA));
		warpEntity.add(new WarpComponent(connectedMap, connectedSpawn));
		entityEngine.addEntity(warpEntity);
		
		warpBody.setUserData(warpEntity);
		
	}
	
}



















