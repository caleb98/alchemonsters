package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.creature.Creature;

public class MoveActionSetVariable implements MoveAction {

	public String variableName;
	public VariableScope scope;
	public String value;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target, CreatureTeam targetTeam) {
		switch(scope) {
		
		case BATTLEFIELD:
			context.variables.setVariable(variableName, value);
			break;
			
		case CREATURE:
			source.variables.setVariable(variableName, value);
			break;
			
		case ENEMY_CREATURE:
			target.variables.setVariable(variableName, value);
			break;
			
		case ENEMY_TEAM:
			targetTeam.variables.setVariable(variableName, value);
			break;
			
		case TEAM:
			sourceTeam.variables.setVariable(variableName, value);
			break;
		
			
		}
	}
	
	public enum VariableScope {
		CREATURE,
		ENEMY_CREATURE,
		TEAM,
		ENEMY_TEAM,
		BATTLEFIELD,
	}

}
