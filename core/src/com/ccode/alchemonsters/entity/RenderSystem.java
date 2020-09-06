package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.ccode.alchemonsters.AlchemonstersGame;
import com.ccode.alchemonsters.engine.OrthogonalTiledSpriteMapRenderer;

public class RenderSystem extends IteratingSystem {
	
	private AlchemonstersGame game;
	
	private OrthogonalTiledSpriteMapRenderer renderer;
	private float lifetime = 0;
	
	//test
	private static BitmapFont font = new BitmapFont();
	private static GlyphLayout interactTextLayout = new GlyphLayout(font, "E");
	private static GlyphLayout dialogueTextLayout = new GlyphLayout();
	
	public RenderSystem(AlchemonstersGame game, TiledMap map, SpriteBatch batch) {
		super(Family.all(TransformComponent.class)
				.one(AnimationComponent.class, TextureComponent.class).get());
		renderer = new OrthogonalTiledSpriteMapRenderer(map, batch, this);
		this.game = game;
	}
	
	@Override
	public void update(float deltaTime) {
		lifetime += deltaTime;
		renderer.setView(game.graphicsCamera);
		renderer.render();
		super.update(deltaTime);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
	}
	
	public void renderObjects(Batch batch) {
		for(Entity e : getEntities()) {
			
			TextureRegion texture;
			float originX, originY;
			TransformComponent transform = Mappers.transformComponent.get(e);
			
			if(Mappers.textureComponent.has(e)) {
				TextureComponent text = Mappers.textureComponent.get(e);
				texture = text.texture;
				originX = text.originX;
				originY = text.originY;
			}
			else if(Mappers.animationComponent.has(e)) {
				AnimationComponent anim = Mappers.animationComponent.get(e);
				originX = anim.originX;
				originY = anim.originY;
				texture = anim.animation.getKeyFrame(lifetime);
			}
			else {
				System.out.println("WARN: Attempted to render entity that contained no TextureComponent or AnimationComponent");
				continue;
			}
			
			batch.draw(
				texture,
				transform.position.x - originX,
				transform.position.y - originY,
				originX,
				originY,
				texture.getRegionWidth(),
				texture.getRegionHeight(),
				transform.scale.x,
				transform.scale.y,
				(float) (180f / Math.PI * transform.rotation)
			);
			
		}
	}
	
	public void renderDialogue(Batch batch) {
		for(Entity e : getEntities()) {			
			if(Mappers.dialogueComponent.has(e)) {
				
				TextureRegion texture;
				if(Mappers.textureComponent.has(e)) {
					TextureComponent text = Mappers.textureComponent.get(e);
					texture = text.texture;
				}
				else if(Mappers.animationComponent.has(e)) {
					AnimationComponent anim = Mappers.animationComponent.get(e);
					texture = anim.animation.getKeyFrame(lifetime);
				}
				else {
					return;
				}
				
				TransformComponent transform = Mappers.transformComponent.get(e);
				DialogueComponent dialogue = Mappers.dialogueComponent.get(e);
				
				if(dialogue.isDialogueActive) {
					DialogueSystem dSystem = getEngine().getSystem(DialogueSystem.class);
					if(dSystem.currentLine != null) {
						dialogueTextLayout.setText(font, dSystem.currentLine.getText());
						font.draw(
								batch, dialogueTextLayout, 
								transform.position.x + (texture.getRegionWidth() / 2) - (dialogueTextLayout.width / 2), 
								transform.position.y + dialogueTextLayout.height + (texture.getRegionHeight() / 2)
							);
					}
				}
				else if(dialogue.showDialogueStartButton) {		
					font.draw(
						batch, interactTextLayout, 
						transform.position.x + (texture.getRegionWidth() / 2), 
						transform.position.y + interactTextLayout.height + (texture.getRegionHeight() / 2)
					);
				}
				
			}
		}
	}
	
}
