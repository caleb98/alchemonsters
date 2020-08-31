package com.ccode.alchemonsters.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class MapInstance {
	
	public String mapName;
	public TiledMap map;
	public World boxWorld;
	public Body playerBody;
	public Engine entityEngine;
	
	MapInstance(String mapName, TiledMap map,  World boxWorld, Body playerBody, Engine entityEngine) {
		this.mapName = mapName;
		this.map = map;
		this.boxWorld = boxWorld;
		this.playerBody = playerBody;
		this.entityEngine = entityEngine;
	}
	
}