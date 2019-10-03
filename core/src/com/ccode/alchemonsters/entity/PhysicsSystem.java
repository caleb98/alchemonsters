package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsSystem extends IntervalIteratingSystem {

	private static final float STEP_TIME = 1.0f / 60.0f;
	private static final int VELOCITY_ITERATIONS = 6;
	private static final int POSITION_ITERATIONS = 2;
	
	private World world;
	
	public PhysicsSystem(World world) {
		super(Family.all(BodyComponent.class, TransformComponent.class).get(), STEP_TIME);
		this.world = world;
	}
	
	@Override
	protected void updateInterval() {
		//Update the physics system
		world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
		
		//Call processEntity on all managed entities
		super.updateInterval();
	}
	
	@Override
	protected void processEntity(Entity entity) {
		//Get components
		TransformComponent transform = Mappers.transformComponent.get(entity);
		BodyComponent body = Mappers.bodyComponent.get(entity);
		
		//Update position and rotation from body
		transform.position.set(body.body.getPosition());
		transform.rotation = body.body.getAngle();
	}
	
}
