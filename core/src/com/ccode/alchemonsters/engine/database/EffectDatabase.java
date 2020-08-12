package com.ccode.alchemonsters.engine.database;

import java.util.HashMap;

import com.ccode.alchemonsters.combat.effect.Effect;

public class EffectDatabase {

	private static final HashMap<String, Class<? extends Effect>> EFFECT_DATABASE = new HashMap<>();
	private static boolean isInitialized = false;
	
	public static void init() {
		//Effect labels and classes added here
		isInitialized = true;
	}
	
	public static Effect getEffect(String effectName) {
		if(!isInitialized) {
			throw new IllegalStateException("Attempted to access effect databse without initialization.");
		}
		
		if(EFFECT_DATABASE.containsKey(effectName)) {
			Class<? extends Effect> effectClass = EFFECT_DATABASE.get(effectName);
			try {
				return effectClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
}
