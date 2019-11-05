package com.ccode.alchemonsters.engine.database;

import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.combat.moves.MoveActionAilmentApplicator;
import com.ccode.alchemonsters.combat.moves.MoveActionChance;
import com.ccode.alchemonsters.combat.moves.MoveActionChooseRandom;
import com.ccode.alchemonsters.combat.moves.MoveActionCombine;
import com.ccode.alchemonsters.combat.moves.MoveActionDamage;
import com.ccode.alchemonsters.combat.moves.MoveActionRepeat;
import com.ccode.alchemonsters.combat.moves.MoveActionScript;
import com.ccode.alchemonsters.combat.moves.MoveActionSetGround;
import com.ccode.alchemonsters.combat.moves.MoveActionSetTerrain;
import com.ccode.alchemonsters.combat.moves.MoveActionSetWeather;
import com.ccode.alchemonsters.combat.moves.MoveActionStatModifier;

public class MoveDatabase {

	private static final String MOVE_DIRECTORY = "moves";
	
	private static HashMap<String, Move> MOVE_DICTIONARY;
	private static boolean isInitialized = false;
	
	public static void initAndLoad() {
		isInitialized = false;
		
		MOVE_DICTIONARY = new HashMap<String, Move>();
		Json json = new Json();
		
		//Set up class aliases
		json.addClassTag("Damage", MoveActionDamage.class);
		json.addClassTag("StatModifier", MoveActionStatModifier.class);
		json.addClassTag("AilmentApplicator", MoveActionAilmentApplicator.class);
		json.addClassTag("Script", MoveActionScript.class);
		json.addClassTag("Chance", MoveActionChance.class);
		json.addClassTag("Repeat", MoveActionRepeat.class);
		json.addClassTag("ChooseRandom", MoveActionChooseRandom.class);
		json.addClassTag("Combine", MoveActionCombine.class);
		json.addClassTag("SetWeather", MoveActionSetWeather.class);
		json.addClassTag("SetTerrain", MoveActionSetTerrain.class);
		json.addClassTag("SetGround", MoveActionSetGround.class);
		
		FileHandle loadDir = Gdx.files.internal(MOVE_DIRECTORY);
		for(FileHandle moveFile : loadDir.list("move")) {
			
			try {
				Move move = json.fromJson(Move.class, moveFile);
				
				if(MOVE_DICTIONARY.containsKey(move.name)) {
					System.err.printf("[Error] Move ID clash for \'%s\'! Move with that name already exists.", move.name);
					continue;
				}
				
				MOVE_DICTIONARY.put(move.name, move);
				System.out.printf("Move \'%s\' loaded.\n", move.name);
			} catch (SerializationException se) {
				System.err.printf("[Error] Unable to load move file %s! Caused by %s\n", moveFile.name(), se.getCause());
				se.printStackTrace(System.err);
			}
			
		}
		
		isInitialized = true;
	}
	
	/**
	 * Gets a Move from the dictionary using the move's
	 * name. If no move with the given name is found  an
	 * error message will be printed and null will be 
	 * returned,  but no exception will be thrown.
	 * Must call MoveDictionary.initAndLoad() prior
	 * to using this function.
	 * @param moveName
	 * @return
	 */
	public static Move getMove(String moveName) {
		if(!isInitialized) {
			throw new IllegalStateException("Attempted to retrieve entry from MoveDictionary without initializing.");
		}
		
		if(!MOVE_DICTIONARY.containsKey(moveName)) {
			System.err.printf("[Error] Could not find move of name \'%s\' in MoveDictionary.\n", moveName);
			return null;
		}
		else {
			return MOVE_DICTIONARY.get(moveName);
		}
	}
	
	public static Set<String> getLoadedMoveNames() {
		if(!isInitialized) {
			throw new IllegalStateException("Move dictionary not initialized.");
		}
		
		return MOVE_DICTIONARY.keySet();
	}
	
}