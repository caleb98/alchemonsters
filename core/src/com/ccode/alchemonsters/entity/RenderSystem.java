package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.ccode.alchemonsters.AlchemonstersGame;
import com.ccode.alchemonsters.engine.OrthogonalTiledSpriteMapRenderer;

public class RenderSystem extends IteratingSystem {

	private AlchemonstersGame game;
	
	private OrthogonalTiledSpriteMapRenderer renderer;
	private float lifetime = 0;
	
	public RenderSystem(AlchemonstersGame game, TiledMap map, SpriteBatch batch) {
		super(Family.all(AnimationComponent.class, TransformComponent.class).get());
		renderer = new OrthogonalTiledSpriteMapRenderer(map, batch, this);
		this.game = game;
	}
	
	@Override
	public void update(float deltaTime) {
		lifetime += deltaTime;
		super.update(deltaTime);
		renderer.setView(game.graphicsCamera);
		renderer.render();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
	}
	
	public void renderObjects(Batch batch) {
		for(Entity e : getEntities()) {
			AnimationComponent animation = Mappers.animationComponent.get(e);
			TransformComponent transform = Mappers.transformComponent.get(e);
			
			TextureRegion region = animation.animation.getKeyFrame(lifetime);
			batch.draw(region, transform.position.x, transform.position.y);
		}
	}
	
}
