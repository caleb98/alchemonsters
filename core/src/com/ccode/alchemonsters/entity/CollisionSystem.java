package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ccode.alchemonsters.TestWorldScreen;
import com.ccode.alchemonsters.entity.TypeComponent.Type;

public class CollisionSystem extends IteratingSystem {

	private TestWorldScreen worldScreen;
	
	public CollisionSystem(TestWorldScreen worldScreen) {
		super(Family.all(CollisionComponent.class).get());
		this.worldScreen = worldScreen;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CollisionComponent col = Mappers.collisionComponent.get(entity);
		
		while(!col.collisions.isEmpty()) {
			Entity collided = col.collisions.poll();
					
			if(collided != null) {
				TypeComponent type = Mappers.typeComponent.get(collided);
				
				//If the collided entity has no type, ignore the collision
				if(type == null) {
					collided = null;
					return;
				}
				else if(type.type == Type.WARP_AREA) {
					WarpComponent warp = Mappers.warpComponent.get(collided);
					worldScreen.switchToMap(warp.warpMap, warp.warpSpawn);
				}
			}
		}
	}

}
