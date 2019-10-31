package com.ccode.alchemonsters;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class OrthogonalTiledSpriteMapRenderer extends OrthogonalTiledMapRenderer {

	private ArrayList<Sprite> sprites;
	
	public OrthogonalTiledSpriteMapRenderer(TiledMap map, SpriteBatch batch) {
		super(map, batch);
		sprites = new ArrayList<Sprite>();
	}
	
	public void addSprite(Sprite s) {
		sprites.add(s);
	}
	
	public void removeSprite(Sprite s) {
		sprites.remove(s);
	}
	
	@Override
	public void render() {
		beginRender();
		for(MapLayer layer : map.getLayers()) {
			renderMapLayer(layer);
			if(layer.getName().equals("scene_bg")) {
				for(Sprite s : sprites) {
					s.draw(batch);
				}
			}
		}
		endRender();
	}
	
}
