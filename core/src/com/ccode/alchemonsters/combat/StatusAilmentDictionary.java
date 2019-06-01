package com.ccode.alchemonsters.combat;

import java.util.HashMap;

import com.ccode.alchemonsters.creature.Creature;

public class StatusAilmentDictionary {

	private static HashMap<String, StatusAilmentEffect> STATUS_AILMENTS;
	private static boolean isInitialized = false;
	
	public static void init() {
		STATUS_AILMENTS = new HashMap<String, StatusAilmentEffect>();
		
		//Initialize all ailments here
		registerAilments(
		
		new StatusAilmentEffect("Toxic") {
			public void startPhase(Creature source, Creature target) {
				target.currentHealth -= target.maxHealth / 8;
			}
		}
				
		);
		
		isInitialized = true;
	}
	
	public static void registerAilments(StatusAilmentEffect... ailments) {
		for(StatusAilmentEffect ailment : ailments) {
			if(STATUS_AILMENTS.containsKey(ailment.name)) {
				System.err.printf("[ERROR] Unable to register new status ailment with name \'%s\'. An ailment with this name already exists.\n", ailment.name);
				continue;
			}
			
			STATUS_AILMENTS.put(ailment.name, ailment);
			System.out.printf("Registered ailment \'%s\'.\n", ailment.name);
		}
	}
	
	public static StatusAilmentEffect getAilment(String name) {
		if(!isInitialized) {
			throw new IllegalStateException("StatusAilmentDictionary not intialized.");
		}
		
		if(!STATUS_AILMENTS.containsKey(name)) {
			System.err.printf("[Error] Unable to find status ailment of name \'%s\' in StatusAilmentDictionary.\n", name);
			return null;
		}
		else {
			return STATUS_AILMENTS.get(name);
		}
	}
	
}
