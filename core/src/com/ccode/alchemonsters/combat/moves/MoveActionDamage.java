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
		boolean isHit = rollHit(move, context, source, target);
		//Check for crit
		boolean isCrit = rollCrit(move, context, source, target);
		//STAB
		boolean isStab = checkStab(move, context, source, target);
		
		if(isHit) {
			switch(this.target) {
			
			case OPPONENT:
				int damage = getDamageAgainst(move, context, source, target, power, isCrit, isStab);
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
	
	public boolean rollHit(Move move, BattleContext context, Creature source, Creature target) {
		return GameRandom.nextFloat() < move.accuracy;
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
		float sourceAttack = move.moveType == MoveType.MAGIC ? source.getBuffedMagicAtk() : source.getBuffedPhysAtk();
		float targetDefense = move.moveType == MoveType.MAGIC ? target.getBuffedMagicDef() : target.getBuffedPhysDef();	
		
		float rawDamage = ((200 + (sourceAttack - 200)) * 2.9f * power) / 200f;
		
		float defenseReduction = rawDamage * (targetDefense / (targetDefense + 0.5f * rawDamage)) + (targetDefense * power) / 1500f;
		float penResRatio = source.getBuffedPen() / (float) target.getBuffedRes();
		
		float actual = Math.max((rawDamage - defenseReduction) * penResRatio * (11 - source.currentLevel / 10f), 1);
		
		//TODO: apply these buffs before defenses?
		//TODO: variable crit multiplier values3
		if(isCrit) {
			actual *= 1.5f;
		}
		
		//TODO: variable stab multiplier values?
		if(isStab) {
			actual *= STAB_MULTIPLIER;
		}
		
		float random = 0.8f + GameRandom.nextFloat() * 0.4f;
		actual *= random;
		
		return (int) actual;
	}
	
}























