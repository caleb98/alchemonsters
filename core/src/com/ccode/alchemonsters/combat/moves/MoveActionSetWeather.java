package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.engine.event.messages.MCombatWeatherChanged;

public class MoveActionSetWeather implements MoveAction {

	/**
	 * The weather type that should be set to. 
	 */
	public WeatherType weather;
	
	@Override
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		WeatherType old = moveInstance.context.battleground.weather;
		moveInstance.context.battleground.weather = weather;
		publish(new MCombatWeatherChanged(moveInstance.context, moveInstance.source, moveInstance.move.name, old, weather));
	}
	
}
