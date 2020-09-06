package com.ccode.alchemonsters.entity;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ccode.alchemonsters.TestWorldScreen;
import com.ccode.alchemonsters.entity.CollisionComponent.CollisionData;
import com.ccode.alchemonsters.entity.ObjectTypeComponent.ObjectType;

public class CollisionSystem extends IteratingSystem {

	public static final int GROUP_UNIT = -1;
	public static final int GROUP_COLLISION_BOX = 1;
	public static final int GROUP_ADJACENT_SENSOR = 2;
	
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
				
				CollisionData colData = col.collisions.poll();
				Entity collided = colData.collidedEntity;
				ObjectTypeComponent collidedType = Mappers.objectTypeComponent.get(collided);
				
				if(collidedType.type == ObjectType.WARP_AREA) {
					WarpComponent warp = Mappers.warpComponent.get(collided);
					worldScreen.switchToMap(warp.warpMap, warp.warpSpawn);
					//We've encountered a warp collision, so any other warp collisions
					//at this point are uneccesary copies. Remove them.
					Iterator<CollisionData> iter = col.collisions.iterator();
					while(iter.hasNext()) {
						CollisionData checkData = iter.next();
						Entity check = checkData.collidedEntity;
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
				
				CollisionData colData = col.collisions.poll();
				Entity collided = colData.collidedEntity;
				ObjectTypeComponent collidedType = Mappers.objectTypeComponent.get(collided);
				
				if(collidedType.type == ObjectType.COLLISION_BOX) {
					worldScreen.removeEntity(entity);
				}
				
			}
		}
		
	}

}
