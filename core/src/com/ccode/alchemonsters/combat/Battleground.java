package com.ccode.alchemonsters.combat;

public class Battleground {
	
	public GroundType ground;
	public TerrainType terrain;
	public WeatherType weather;
	
	public Battleground() {
		ground = GroundType.NORMAL;
		terrain = TerrainType.NORMAL;
		weather = WeatherType.NORMAL;
	}

}
