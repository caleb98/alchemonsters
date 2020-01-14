package com.ccode.alchemonsters.creature;

import java.util.Random;

import com.ccode.alchemonsters.engine.database.CreatureDatabase;
import com.ccode.alchemonsters.util.GameRandom;

public class CreatureFactory {

	private static Random rand = new Random();
	
	//Stat gen values
	private static double STAT_GEN_MEAN = 16;
	private static double STAT_GEN_SD = 6;
	
	public static Creature generateRandomLevelOne(String baseID) {
		Creature gen = new Creature();
		
		CreatureBase base = CreatureDatabase.getBase(baseID);
		CreatureNature nature = generateRandomCreatureNature();
		
		int baseHealth = base.minBaseHealth + GameRandom.nextInt(base.maxBaseHealth - base.minBaseHealth);
		int baseMana = base.minBaseMana + GameRandom.nextInt(base.maxBaseMana - base.minBaseMana);
		
		gen.base = base;
		gen.nature = nature;
		gen.baseHealth = baseHealth;
		gen.baseMana = baseMana;
		
		gen.vitaeAttunement = generateRandomStatValue();
		gen.focusAttunement = generateRandomStatValue();
		gen.magicAtkAttunement = generateRandomStatValue();
		gen.magicDefAttunement = generateRandomStatValue();
		gen.physAtkAttunement = generateRandomStatValue();
		gen.physDefAttunement = generateRandomStatValue();
		gen.speedAttunement = generateRandomStatValue();
		gen.penAttunement = generateRandomStatValue();
		gen.resAttunement = generateRandomStatValue();
		
		gen.recalculateAllStats(true);
		
		return gen;
	}
	
	public static CreatureNature generateRandomCreatureNature() {
		return new CreatureNature(
				StatType.primaries[rand.nextInt(StatType.primaries.length)], 
				StatType.primaries[rand.nextInt(StatType.primaries.length)]);
	}
	
	/*
	 * Graph to adjust values for gen mean and SD:
	 * https://www.desmos.com/calculator/bluepthxcb
	 */
	public static byte generateRandomStatValue() {
		double dev = rand.nextGaussian();
		double value = STAT_GEN_MEAN + dev * STAT_GEN_SD;
		if(value > 31) value = 31;
		if(value < 0) value = 0;
		
		return (byte) Math.round(value);
	}
	
}
