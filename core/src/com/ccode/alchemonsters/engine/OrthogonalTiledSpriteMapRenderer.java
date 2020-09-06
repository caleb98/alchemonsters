package com.ccode.alchemonsters.engine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.ccode.alchemonsters.entity.RenderSystem;

public class OrthogonalTiledSpriteMapRenderer extends OrthogonalTiledMapRenderer {
	
	private RenderSystem renderSystem;
	
	public OrthogonalTiledSpriteMapRenderer(TiledMap map, SpriteBatch batch, RenderSystem system) {
		super(map, batch);
		renderSystem = system;
	}
	
	@Override
	public void render() {
		beginRender();
		for(MapLayer layer : map.getLayers()) {
			renderMapLayer(layer);
			if(layer.getName().equals("scene_bg")) {
				renderSystem.renderObjects(batch);
			}
		}
		renderSystem.renderDialogue(batch);
		endRender();
	}
	
}
