package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Component;

public class TypeComponent implements Component {

	public Type type;
	
	public TypeComponent(Type type) {
		this.type = type;
	}
	
	public enum Type {
		//"real" objects
		SCENERY,
		UNIT,
		
		//"meta" objects
		WARP_AREA,
		COLLISION_BOX,
	}
	
}
