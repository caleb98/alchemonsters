package com.ccode.alchemonsters.entity;

import java.util.LinkedList;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class CollisionComponent implements Component {

	public LinkedList<Entity> collisions = new LinkedList<>();
	
}
