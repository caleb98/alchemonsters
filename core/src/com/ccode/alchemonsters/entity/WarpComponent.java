package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Component;

public class WarpComponent implements Component {

	public String warpMap;
	public String warpSpawn;
	
	public WarpComponent(String loc, String spawn) {
		warpMap = loc;
		warpSpawn = spawn;
	}

}
