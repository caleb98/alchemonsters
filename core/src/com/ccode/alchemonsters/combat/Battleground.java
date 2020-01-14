package com.ccode.alchemonsters.combat;

public class Battleground {
	
	public TerrainType terrain;
	public BiomeType biome;
	public WeatherType weather;
	
	public Battleground() {
		terrain = TerrainType.NORMAL;
		biome = BiomeType.NORMAL;
		weather = WeatherType.NORMAL;
	}

}
