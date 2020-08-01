package com.ccode.alchemonsters.engine.database;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.ccode.alchemonsters.combat.effect.Ailment;
import com.ccode.alchemonsters.combat.effect.BlightAilment;
import com.ccode.alchemonsters.combat.effect.BurnAilment;
import com.ccode.alchemonsters.combat.effect.ScorchAilment;

public class AilmentDatabase {

	private static final HashMap<String, Class<? extends Ailment>> AILMENT_DATABASE = new HashMap<>();
	private static boolean isInitialized = false;
	
	public static void init() {
		//Ailment labels and classes added here
		AILMENT_DATABASE.put("Blight", BlightAilment.class);
		AILMENT_DATABASE.put("Burn", BurnAilment.class);
		AILMENT_DATABASE.put("Scorch", ScorchAilment.class);
		isInitialized = true;
	}
	
	public static Ailment getAilment(String ailmentName, int duration) {
		if(!isInitialized) {
			throw new IllegalStateException("Attempted to access ailment database without initialization.");
		}
		
		if(AILMENT_DATABASE.containsKey(ailmentName)) {
			Class<? extends Ailment> ailmentClass = AILMENT_DATABASE.get(ailmentName);
			try {
				return ailmentClass.getConstructor(int.class).newInstance(duration);
			} catch (InstantiationException | 
					 IllegalAccessException | 
					 IllegalArgumentException | 
					 InvocationTargetException | 
					 NoSuchMethodException | 
					 SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
}
