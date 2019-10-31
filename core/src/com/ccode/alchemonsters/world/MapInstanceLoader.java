package com.ccode.alchemonsters.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ShortArray;
import com.ccode.alchemonsters.AlchemonstersGame;
import com.ccode.alchemonsters.OrthogonalTiledSpriteMapRenderer;
import com.ccode.alchemonsters.TestWorldScreen;
import com.ccode.alchemonsters.entity.BodyComponent;
import com.ccode.alchemonsters.entity.CollisionComponent;
import com.ccode.alchemonsters.entity.CollisionSystem;
import com.ccode.alchemonsters.entity.PhysicsSystem;
import com.ccode.alchemonsters.entity.TransformComponent;
import com.ccode.alchemonsters.entity.TypeComponent;
import com.ccode.alchemonsters.entity.TypeComponent.Type;
import com.ccode.alchemonsters.entity.WarpComponent;

public class MapInstanceLoader {

	private static final TmxMapLoader MAP_LOADER = new TmxMapLoader();
	private static final EarClippingTriangulator TRIANGULATOR = new EarClippingTriangulator();
	private static final int ELLIPSE_STEPS = 32;
	
	public static MapInstance loadMapInstance(AlchemonstersGame game, TestWorldScreen world, String mapName, String spawnId) {
		//Load the map
		TiledMap map = MAP_LOADER.load(String.format("maps/%s.tmx", mapName));
		OrthogonalTiledSpriteMapRenderer renderer = new OrthogonalTiledSpriteMapRenderer(map, game.batch);
		
		//Create box2d world
		World boxWorld = new World(new Vector2(0, 0), true);
		boxWorld.setContactListener(world);
		
		//Create the entity engine
		Engine entityEngine = new Engine();
		entityEngine.addSystem(new PhysicsSystem(boxWorld));
		entityEngine.addSystem(new CollisionSystem(world));
		
		//Add the collision boxes to the world/entity system
		if(map.getLayers().get("collision") != null) {
			for(MapObject o : map.getLayers().get("collision").getObjects()) {
				
				createCollisionObject(o, boxWorld, entityEngine);
				
			}
		}
		
		//Add warp area boxes to the world/entity system
		if(map.getLayers().get("warps") != null) {
			for(MapObject o : map.getLayers().get("warps").getObjects()) {
			
				createWarpObject(o, boxWorld, entityEngine, mapName);
				
			}
		}
		
		//Add player at spawn (to box2d world/entity engine)		
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
		
		Entity playerEntity = new Entity();
		
		BodyDef pBodyDef = new BodyDef();
		pBodyDef.type = BodyType.DynamicBody;
		pBodyDef.position.set(playerSpawn);
		
		Body pBody = boxWorld.createBody(pBodyDef);
		CircleShape pShape = new CircleShape();
		pShape.setRadius(16);
		pShape.setPosition(new Vector2(16, 16));
		pBody.createFixture(pShape, 0.0f);
		pShape.dispose();
		
		playerEntity.add(new BodyComponent(pBody));
		playerEntity.add(new TransformComponent());
		playerEntity.add(new CollisionComponent());
		playerEntity.add(new TypeComponent(Type.UNIT));
		entityEngine.addEntity(playerEntity);
		
		pBody.setUserData(playerEntity);
		
		//Combine into map instance
		return new MapInstance(mapName, map, renderer, boxWorld, pBody, entityEngine);
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
			colBody.createFixture(colShape, 0.0f);
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
			colBody.createFixture(colShape, 0.0f);
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
				colBody.createFixture(colShape, 0.0f);
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
			colBody.createFixture(colShape, 0.0f);
			colShape.dispose();
			
		}
		else {
			
			System.out.println("[Error] Attempted to create collision object from incompatible collision shape!");
			return;
			
		}
		
		collisionEntity.add(new BodyComponent(colBody));
		collisionEntity.add(new TransformComponent());
		collisionEntity.add(new TypeComponent(Type.COLLISION_BOX));
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
		warpEntity.add(new TypeComponent(Type.WARP_AREA));
		warpEntity.add(new WarpComponent(connectedMap, connectedSpawn));
		entityEngine.addEntity(warpEntity);
		
		warpBody.setUserData(warpEntity);
		
	}
	
}



















