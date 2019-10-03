package com.ccode.alchemonsters;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;

public class OrthoCachedTiledMapSpriteRenderer extends OrthoCachedTiledMapRenderer {

	public OrthoCachedTiledMapSpriteRenderer(TiledMap map) {
		super(map);
		blending = true;
	}

	@Override
	public void renderTileLayer(TiledMapTileLayer layer) {
		super.renderTileLayer(layer);
		for(MapObject o : layer.getObjects()) {
			if(o instanceof TextureMapObject) {
				TextureMapObject tex = (TextureMapObject) o;
				spriteCache.add(tex.getTextureRegion(), tex.getX(), tex.getY());
			}
		}
	}
	
}
