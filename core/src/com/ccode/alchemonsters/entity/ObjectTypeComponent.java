package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Component;

public class ObjectTypeComponent implements Component {

	public ObjectType type;
	
	public ObjectTypeComponent(ObjectType type) {
		this.type = type;
	}
	
	public enum ObjectType {
		//"real" objects
		SCENERY,
		UNIT,
		PROJECTILE,
		
		//"meta" objects
		WARP_AREA,
		COLLISION_BOX,
	}
	
}
