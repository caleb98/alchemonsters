package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.ComponentMapper;

public class Mappers {

	public static final ComponentMapper<BodyComponent> bodyComponent = ComponentMapper.getFor(BodyComponent.class);
	public static final ComponentMapper<TransformComponent> transformComponent = ComponentMapper.getFor(TransformComponent.class);
	public static final ComponentMapper<CollisionComponent> collisionComponent = ComponentMapper.getFor(CollisionComponent.class);
	public static final ComponentMapper<TypeComponent> typeComponent = ComponentMapper.getFor(TypeComponent.class);
	public static final ComponentMapper<WarpComponent> warpComponent = ComponentMapper.getFor(WarpComponent.class);
	public static final ComponentMapper<AnimationComponent> animationComponent = ComponentMapper.getFor(AnimationComponent.class);
	
}
