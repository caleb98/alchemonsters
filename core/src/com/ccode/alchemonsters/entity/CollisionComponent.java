package com.ccode.alchemonsters.entity;

import java.util.LinkedList;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;

public class CollisionComponent implements Component {

	public LinkedList<CollisionData> collisions = new LinkedList<>();
	
	public void addCollision(Fixture myFixture, Fixture collidedFixture, Entity collidedEntity) {
		collisions.add(new CollisionData(myFixture, collidedFixture, collidedEntity));
	}
	
	public static class CollisionData {
		
		public Fixture myFixture;
		public Fixture collidedFixture;
		public Entity collidedEntity;
		
		public CollisionData(Fixture myFixture, Fixture collidedFixture, Entity collidedEntity) {
			this.myFixture = myFixture;
			this.collidedFixture = collidedFixture;
			this.collidedEntity = collidedEntity;
		}
		
	}
	
}
