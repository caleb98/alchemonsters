package com.ccode.alchemonsters.creature;

import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

public class CreatureDictionary {

	private static final String CREATURE_DIRECTORY = "creatures";
	
	private static HashMap<String, CreatureBase> CREATURE_DICTIONARY;
	private static boolean isInitialized = false;
	
	public static void initAndLoad() {
		CREATURE_DICTIONARY = new HashMap<String, CreatureBase>();
		Json json = new Json();
		
		FileHandle loadDir = Gdx.files.internal(CREATURE_DIRECTORY);
		for(FileHandle creatureFile : loadDir.list("creature")) {
			
			try {
				CreatureBase base = json.fromJson(CreatureBase.class, creatureFile);
				
				if(CREATURE_DICTIONARY.containsKey(base.id)) {
					System.err.printf("[Error] Creature ID clash for %s! Creature %s already exists with that ID (%s)\n", 
							base.name, 
							CREATURE_DICTIONARY.get(base.id),
							base.id
					);
					continue;
				}
				
				CREATURE_DICTIONARY.put(base.id, base);
				System.out.printf("Creature '%s' loaded with ID %s\n", base.name, base.id);
			} catch (SerializationException se) {
				System.err.printf("[Error] Unable to load creature file %s! Caused by %s\n", creatureFile.name(), se.getCause());
			}
			
		}
		
		isInitialized = true;
	}
	
	/**
	 * Gets a CreatureBase by ID from the dictionary. 
	 * If no base with the given ID is found an error message will be printed and 
	 * null will be returned, but no error will be thrown.
	 * @param baseID ID of creature base
	 * @return CreatureBase
	 */
	public static CreatureBase getBase(String baseID) {
		if(!isInitialized) {
			throw new IllegalStateException("Attempted to retrieve entry from CreatureDictionary without initializing.");
		}
		
		if(!CREATURE_DICTIONARY.containsKey(baseID)) {
			System.err.printf("[Error] Could not find creature with ID '%s' in CreatureDictionary.\n", baseID);
			return null;
		}
		else {
			return CREATURE_DICTIONARY.get(baseID);
		}
	}
	
	public static Set<String> getAvailableCreatureIDs() {
		return CREATURE_DICTIONARY.keySet();
	}
	
}
