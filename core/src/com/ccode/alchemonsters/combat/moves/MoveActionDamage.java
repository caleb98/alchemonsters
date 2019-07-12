package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionDamage implements MoveAction {
	
	public static final float STAB_MULTIPLIER = 1.5f;
	
	public MoveTarget target;
	public int power;
	public float accuracy;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target, CreatureTeam targetTeam) {
		//Check accuracy/hit chance
		boolean isHit = GameRandom.nextFloat() < move.accuracy;
		
		//Check for crit
		boolean isCrit = false;
		for(int i = 0; i < move.critStage; ++i) {
			if(GameRandom.nextFloat() < 0.05f) {
				isCrit = true;
				break;
			}
		}
		
		//STAB
		boolean isStab = false;
		for(ElementType e : source.base.types) {
			if(e == move.elementType) {
				isStab = true;
				break;
			}
		}
		
		
		
		if(isHit) {
			switch(this.target) {
			
			case OPPONENT:
				int damage = getDamageAgainst(move, source, target, isCrit, isStab);
				target.currentHealth -= damage;
				publish(new MCombatDamageDealt(context, source, target, move.name, move.elementType, damage, isHit, isCrit, false));
				break;
				
			case OPPONENT_TEAM:
				//TODO: team damage
				break;
				
			case SELF:
				//TODO: self damage 
				break;
				
			case SELF_TEAM:
				//TODO: self team damage
				break;
			
			}
		}
	}
	
	private int getDamageAgainst(Move move, Creature source, Creature target, boolean isCrit, boolean isStab) {
		float attackDefenseRatio = (move.moveType == MoveType.MAGIC) ?
				source.getCurrentMagicAtk() / target.getCurrentMagicDef() :
				source.getCurrentPhysAtk() / target.getCurrentPhysDef();
				
		float min = ((float) (power * source.getCurrentMagicAtk()) / 32f + 1) * attackDefenseRatio * 0.8f;
		float max = ((float) (power * source.getCurrentMagicAtk()) / 32f + 1) * attackDefenseRatio * 1.2f;
		float actual = min + GameRandom.nextFloat() * (max - min);
		
		//TODO: variable crit multiplier values
		if(isCrit) {
			actual *= 1.5f;
		}
		
		//TODO: variable stab multiplier values?
		if(isStab) {
			actual *= STAB_MULTIPLIER;
		}
		
		int floored = (int) Math.floor(actual);
		
		return floored;
	}
	
}
