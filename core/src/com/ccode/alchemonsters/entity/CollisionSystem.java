package com.ccode.alchemonsters.entity;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ccode.alchemonsters.TestWorldScreen;
import com.ccode.alchemonsters.entity.ObjectTypeComponent.ObjectType;

public class CollisionSystem extends IteratingSystem {

	public static final int GROUP_WORLD_OBJECT = -1;
	public static final int GROUP_COLLISION_BOX = 1;
	
	private TestWorldScreen worldScreen;
	
	public CollisionSystem(TestWorldScreen worldScreen) {
		super(Family.all(CollisionComponent.class, ObjectTypeComponent.class).get());
		this.worldScreen = worldScreen;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		CollisionComponent col = Mappers.collisionComponent.get(entity);
		ObjectTypeComponent type = Mappers.objectTypeComponent.get(entity);
		
		//COLLISION CODE FOR PLAYER
		if(Mappers.playerComponent.has(entity)) {
			while(!col.collisions.isEmpty()) {
				
				Entity collided = col.collisions.poll();
				ObjectTypeComponent collidedType = Mappers.objectTypeComponent.get(collided);
				
				if(collidedType.type == ObjectType.WARP_AREA) {
					WarpComponent warp = Mappers.warpComponent.get(collided);
					worldScreen.switchToMap(warp.warpMap, warp.warpSpawn);
					//We've encountered a warp collision, so any other warp collisions
					//at this point are uneccesary copies. Remove them.
					Iterator<Entity> iter = col.collisions.iterator();
					while(iter.hasNext()) {
						Entity check = iter.next();
						ObjectTypeComponent checkType = Mappers.objectTypeComponent.get(check);
						if(checkType != null && checkType.type == ObjectType.WARP_AREA) {
							iter.remove();
						}
					}
				}
				
			}
		}
		
		//PROJECTILE COLLISION CODE
		else if(type.type == ObjectType.PROJECTILE) {
			while(!col.collisions.isEmpty()) {
				
				Entity collided = col.collisions.poll();
				ObjectTypeComponent collidedType = Mappers.objectTypeComponent.get(collided);
				
				if(collidedType.type == ObjectType.COLLISION_BOX) {
					worldScreen.removeEntity(entity);
				}
				
			}
		}
		
	}

}
