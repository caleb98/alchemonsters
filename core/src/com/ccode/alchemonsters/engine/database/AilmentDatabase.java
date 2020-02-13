package com.ccode.alchemonsters.engine.database;

import java.util.HashMap;

import com.ccode.alchemonsters.combat.effect.Effect;
import com.ccode.alchemonsters.combat.effect.TestEffect;

public class AilmentDatabase {

	private static final HashMap<String, Class<? extends Effect>> AILMENT_DATABASE = new HashMap<>();
	private static boolean isInitialized = false;
	
	public static void init() {
		AILMENT_DATABASE.put("Test Effect", TestEffect.class);
		isInitialized = true;
	}
	
	public static Effect getEffect(String effectName) {
		if(!isInitialized) {
			throw new IllegalStateException("Attempted to access ailment database without initialization.");
		}
		
		if(AILMENT_DATABASE.containsKey(effectName)) {
			Class<? extends Effect> effectClass = AILMENT_DATABASE.get(effectName);
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
