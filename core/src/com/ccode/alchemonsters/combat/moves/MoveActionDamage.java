package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.context.BattleContext;
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
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		
		//Check accuracy/hit chance
		boolean isHit;
		//Check for crit
		boolean isCrit;
		//STAB
		boolean isStab = moveInstance.checkStab();
		
		int damage;
		
		switch(this.target) {
		
		case TARGET:
			for(Creature tar : moveInstance.targets) {
				isHit = moveInstance.rollHit();
				isCrit = moveInstance.rollCrit();
				
				if(isHit) {
					damage = getDamageAgainst(moveInstance, tar, power, isCrit, isStab);
					tar.modifyHealth(-damage);
					publish(new MCombatDamageDealt(moveInstance.context, moveInstance.source, tar, moveInstance.move.name, moveInstance.getElementType(), damage, isCrit, isStab, false));
				}
				//TODO: action miss message?
			}
			break;
			
		case OPPONENT_TEAM:
			Creature opp;
			for(int i = 0; i < opponentTeam.numActives; ++i) {
				isHit = moveInstance.rollHit();
				isCrit = moveInstance.rollCrit();
				
				if(isHit) {
					opp = opponentTeam.get(i);
					damage = getDamageAgainst(moveInstance, opp, power, isCrit, isStab);
					opp.modifyHealth(-damage);
					publish(new MCombatDamageDealt(moveInstance.context, moveInstance.source, opp, moveInstance.move.name, moveInstance.getElementType(), damage, isCrit, isStab, false));
				}
			}
			break;
			
		case SELF:
			isHit = moveInstance.rollHit();
			isCrit = moveInstance.rollCrit();
			
			if(isHit) {
				damage = getDamageAgainst(moveInstance, moveInstance.source, power, isCrit, isStab);
				moveInstance.source.modifyHealth(-damage);
				publish(new MCombatDamageDealt(moveInstance.context, moveInstance.source, moveInstance.source, moveInstance.move.name, moveInstance.getElementType(), damage, isCrit, isStab, false));
			}
			break;
			
		case SELF_TEAM:
			Creature friendly;
			for(int i = 0; i < sourceTeam.numActives; ++i) {
				isHit = moveInstance.rollHit();
				isCrit = moveInstance.rollCrit();
				
				if(isHit) {
					friendly = sourceTeam.get(i);
					damage = getDamageAgainst(moveInstance, friendly, power, isCrit, isStab);
					friendly.modifyHealth(-damage);
					publish(new MCombatDamageDealt(moveInstance.context, moveInstance.source, friendly, moveInstance.move.name, moveInstance.getElementType(), damage, isCrit, isStab, false));
				}
			}
			break;
		
		}
	}
	
	public static int getDamageAgainst(MoveInstance moveInstance, Creature target, int power, boolean isCrit, boolean isStab) {
		//TODO: check for opponent dodge
		
		BattleContext context = moveInstance.context;
		ElementType elementType = moveInstance.getElementType();
		Creature source = moveInstance.source;
		
		float sourceAttack, targetDefense;
		if(moveInstance.move.moveType != MoveType.NEUTRAL) {
			sourceAttack = moveInstance.move.moveType == MoveType.MAGIC ? source.calcTotalMagicAtk(context) : source.calcTotalPhysAtk(context);
			targetDefense = moveInstance.move.moveType == MoveType.MAGIC ? target.calcTotalMagicDef(context) : target.calcTotalPhysDef(context);
		}
		else {
			sourceAttack = 1;
			targetDefense = 1;
		}
		
		float sourcePen = source.calcTotalPen(context);
		float targetRes = target.calcTotalRes(context);
		
		float atkDefRatio = sourceAttack / targetDefense;
		float resReduction = (targetRes >= sourcePen) ? targetRes - sourcePen : (targetRes - sourcePen) / 4f;
		
		float rawDamage = (power * atkDefRatio - resReduction) * (( (source.currentLevel / 5f) + 1f ) / 21f );
		
		float actual = Math.max(0, rawDamage);
		
		//TODO: apply these buffs before defenses?
		if(isCrit) {
			actual *= source.calcTotalCritMultiplier(context);
		}
		
		if(isStab) {
			actual *= source.calcTotalStabMultiplier(context);
		}
		
		//TODO: globalize deluge water damage bonus and fire damage decrease
		if(context.battleground.weather == WeatherType.DELUGE) {
			if(elementType == ElementType.WATER) {
				actual *= 1.20f;
			}
			else if(elementType == ElementType.FIRE) {
				actual *= 0.8f;
			}
		}
		//TODO: globalize darkness void damage increase amount and fire/air decrease
		else if(context.battleground.weather == WeatherType.DARKNESS) {
			if(elementType == ElementType.VOID) {
				actual *= 1.15f;
			}
			else if(elementType ==ElementType.AIR || elementType == ElementType.FIRE) {
				actual *= 0.9f;
			}
		}
		//TODO: globalize dreamscape fey damage increase 
		else if(context.battleground.weather == WeatherType.DREAMSCAPE) {
			if(elementType == ElementType.FEY) {
				actual *= 1.3f;
			}
		}
		//TODO: globalize sandstorm air attack damage increase amount
		else if(context.battleground.weather == WeatherType.SANDSTORM) {
			if(elementType == ElementType.AIR) {
				actual *= 1.2f;
			}
		}
		//TODO: globalize tempest lightning damage increase
		else if(context.battleground.weather == WeatherType.TEMPEST) {
			if(elementType == ElementType.LIGHTNING) {
				actual *= 1.2f;
			}
		}
		
		float random = 0.8f + GameRandom.nextFloat() * 0.4f;
		actual *= random;
		
		return (int) actual;
	}
	
}























