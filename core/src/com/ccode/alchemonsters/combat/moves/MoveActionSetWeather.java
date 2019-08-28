package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.creature.Creature;

public class MoveActionSetWeather implements MoveAction {

	/**
	 * The weather type that should be set to. 
	 */
	public WeatherType weather;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target,
			CreatureTeam targetTeam) {
		context.battleground.weather = weather;
	}
	
}
