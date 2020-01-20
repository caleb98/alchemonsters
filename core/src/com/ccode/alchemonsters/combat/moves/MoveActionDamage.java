package com.ccode.alchemonsters.combat.moves;

import com.badlogic.gdx.utils.reflect.Field;
import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;
import com.ccode.alchemonsters.util.GameRandom;

public class MoveActionDamage implements MoveAction {
	
	public static final float STAB_MULTIPLIER = 1.5f;
	public static final float CRIT_MULTIPLIER = 1.5f;
	
	/**
	 * The target of the damage.
	 */
	public MoveActionTarget target;
	/**
	 * The strength of the damage. (Not equal to actual damage amount!)
	 */
	public int power;
	
	@Override
	public void activate(Move move, BattleContext context, Creature source, BattleTeam sourceTeam, Creature target, BattleTeam opponentTeam) {
		
		//Check accuracy/hit chance
		boolean isHit = move.rollHit(context, source, target);
		//Check for crit
		boolean isCrit = rollCrit(move, context, source, target);
		//STAB
		boolean isStab = checkStab(move, context, source, target);
		
		int damage;
		
		if(isHit) {
			switch(this.target) {
			
			case TARGET:
				damage = getDamageAgainst(move, context, source, target, power, isCrit, isStab);
				target.modifyHealth(-damage);
				publish(new MCombatDamageDealt(context, source, target, move.name, move.elementType, damage, isHit, isCrit, false));
				break;
				
			case OPPONENT_TEAM:
				Creature opp;
				for(int i = 0; i < opponentTeam.numActives; ++i) {
					opp = opponentTeam.get(i);
					damage = getDamageAgainst(move, context, source, opp, power, isCrit, isStab);
					opp.modifyHealth(-damage);
					publish(new MCombatDamageDealt(context, source, opp, move.name, move.elementType, damage, isHit, isCrit, false));
				}
				break;
				
			case SELF:
				damage = getDamageAgainst(move, context, source, source, power, isCrit, isStab);
				source.modifyHealth(-damage);
				publish(new MCombatDamageDealt(context, source, source, move.name, move.elementType, damage, isHit, isCrit, false));
				break;
				
			case SELF_TEAM:
				Creature friendly;
				for(int i = 0; i < sourceTeam.numActives; ++i) {
					friendly = sourceTeam.get(i);
					damage = getDamageAgainst(move, context, source, friendly, power, isCrit, isStab);
					friendly.modifyHealth(-damage);
					publish(new MCombatDamageDealt(context, source, friendly, move.name, move.elementType, damage, isHit, isCrit, false));
				}
				break;
			
			}
		}
	}
	
	public boolean rollCrit(Move move, BattleContext context, Creature source, Creature target) {
		for(int i = 0; i < move.critStage; ++i) {
			if(GameRandom.nextFloat() < source.critChance) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkStab(Move move, BattleContext context, Creature source, Creature target) {
		for(ElementType e : source.base.types) {
			if(e == move.elementType) {
				return true;
			}
		}
		return false;
	}
	
	public  int getDamageAgainst(Move move, BattleContext context, Creature source, Creature target, int power, boolean isCrit, boolean isStab) {
		float sourceAttack = move.moveType == MoveType.MAGIC ? source.calcTotalMagicAtk(context) : source.calcTotalPhysAtk(context);
		float sourcePen = source.calcTotalPen(context);
		float targetDefense = move.moveType == MoveType.MAGIC ? target.calcTotalMagicDef(context) : target.calcTotalPhysDef(context);	
		float targetRes = target.calcTotalRes(context);
		
		float atkDefRatio = sourceAttack / targetDefense;
		float resReduction = (targetRes >= sourcePen) ? targetRes - sourcePen : (targetRes - sourcePen) / 4f;
		
		float rawDamage = (power * atkDefRatio - resReduction) * (( (source.currentLevel / 5f) + 1f ) / 21f );
		
		float actual = Math.max(0, rawDamage);
		
		//TODO: apply these buffs before defenses?
		//TODO: variable crit multiplier values3
		if(isCrit) {
			actual *= CRIT_MULTIPLIER;
		}
		
		//TODO: variable stab multiplier values?
		if(isStab) {
			actual *= STAB_MULTIPLIER;
		}
		
		//TODO: globalize deluge water damage bonus and fire damage decrease
		if(context.battleground.weather == WeatherType.DELUGE) {
			if(move.elementType == ElementType.WATER) {
				actual *= 1.20f;
			}
			else if(move.elementType == ElementType.FIRE) {
				actual *= 0.8f;
			}
		}
		//TODO: globalize darkness void damage increase amount and fire/air decrease
		else if(context.battleground.weather == WeatherType.DARKNESS) {
			if(move.elementType == ElementType.VOID) {
				actual *= 1.15f;
			}
			else if(move.elementType ==ElementType.AIR || move.elementType == ElementType.FIRE) {
				actual *= 0.9f;
			}
		}
		//TODO: globalize dreamscape fey damage increase 
		else if(context.battleground.weather == WeatherType.DREAMSCAPE) {
			if(move.elementType == ElementType.FEY) {
				actual *= 1.3f;
			}
		}
		//TODO: globalize sandstorm air attack damage increase amount
		else if(context.battleground.weather == WeatherType.SANDSTORM) {
			if(move.elementType == ElementType.AIR) {
				actual *= 1.2f;
			}
		}
		//TODO: globalize tempest lightning damage increase
		else if(context.battleground.weather == WeatherType.TEMPEST) {
			if(move.elementType == ElementType.LIGHTNING) {
				actual *= 1.2f;
			}
		}
		
		float random = 0.8f + GameRandom.nextFloat() * 0.4f;
		actual *= random;
		
		return (int) actual;
	}
	
}























