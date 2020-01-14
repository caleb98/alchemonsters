package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BiomeType;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.event.Message;

public class MCombatWeatherChanged extends Message {

	public static final String ID = "COMBAT_WEATHER_CHANGED";
	
	public final BattleContext context;
	public final Creature source;
	public final String cause;
	public final WeatherType oldWeather;
	public final WeatherType newWeather;
	
	private MCombatWeatherChanged() {
		super(ID);
		context = null;
		source = null;
		cause = null;
		oldWeather = null;
		newWeather = null;
	}
	
	public MCombatWeatherChanged(BattleContext context, Creature source, String cause, WeatherType oldWeather, WeatherType newWeather) {
		super(ID);
		this.context = context;
		this.source = source;
		this.cause = cause;
		this.oldWeather = oldWeather;
		this.newWeather = newWeather;
	}
	
}
