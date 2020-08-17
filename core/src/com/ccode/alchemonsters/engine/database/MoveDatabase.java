package com.ccode.alchemonsters.engine.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.utils.Json;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.combat.moves.MoveAction;
import com.ccode.alchemonsters.combat.moves.MoveActionAilmentApplicator;
import com.ccode.alchemonsters.combat.moves.MoveActionChance;
import com.ccode.alchemonsters.combat.moves.MoveActionChooseRandom;
import com.ccode.alchemonsters.combat.moves.MoveActionCombine;
import com.ccode.alchemonsters.combat.moves.MoveActionDamage;
import com.ccode.alchemonsters.combat.moves.MoveActionRepeat;
import com.ccode.alchemonsters.combat.moves.MoveActionScript;
import com.ccode.alchemonsters.combat.moves.MoveActionSetBiome;
import com.ccode.alchemonsters.combat.moves.MoveActionSetTerrain;
import com.ccode.alchemonsters.combat.moves.MoveActionSetWeather;
import com.ccode.alchemonsters.combat.moves.MoveActionStatModifier;
import com.ccode.alchemonsters.combat.moves.MoveActionTarget;
import com.ccode.alchemonsters.combat.moves.MoveTargetSelectType;
import com.ccode.alchemonsters.combat.moves.MoveType;
import com.ccode.alchemonsters.combat.moves.TurnType;
import com.ccode.alchemonsters.creature.ElementType;

public class MoveDatabase {
	
	private static final String ACTION_DEFINITIONS_DIRECTORY = "actiondefs";
	
	private static HashMap<String, Move> MOVE_DICTIONARY;
	private static boolean isInitialized = false;
	
	public static void init() {
		try {
			MOVE_DICTIONARY = new HashMap<String, Move>();
			Json json = new Json();
			
			//Set up class aliases
			json.addClassTag("Damage", MoveActionDamage.class);
			json.addClassTag("StatModifier", MoveActionStatModifier.class);
			json.addClassTag("Script", MoveActionScript.class);
			json.addClassTag("Chance", MoveActionChance.class);
			json.addClassTag("Repeat", MoveActionRepeat.class);
			json.addClassTag("ChooseRandom", MoveActionChooseRandom.class);
			json.addClassTag("Combine", MoveActionCombine.class);
			json.addClassTag("SetWeather", MoveActionSetWeather.class);
			json.addClassTag("SetBiome", MoveActionSetBiome.class);
			json.addClassTag("SetTerrain", MoveActionSetTerrain.class);
			json.addClassTag("AilmentApplicator", MoveActionAilmentApplicator.class);
			
			ResultSet moves = GameData.executeQuery("SELECT * FROM Moves");
			String actionJson;
			
			final Field mName;
			final Field mDesc;
			final Field mAccuracy;
			final Field mManaCost;
			final Field mCritStage;
			final Field mElementType;
			final Field mMoveType;
			final Field mTurnType;
			final Field mTargetSelectType;
			
			final Field mPriority;
			final Field mDelayAmount;
			
			final Field mActions;
			
			try {
				
				mName = Move.class.getDeclaredField("name");
				mName.setAccessible(true);
				
				mDesc = Move.class.getDeclaredField("desc");
				mDesc.setAccessible(true);
				
				mAccuracy = Move.class.getDeclaredField("accuracy");
				mAccuracy.setAccessible(true);
				
				mManaCost = Move.class.getDeclaredField("manaCost");
				mManaCost.setAccessible(true);
				
				mCritStage = Move.class.getDeclaredField("critStage");
				mCritStage.setAccessible(true);
				
				mElementType = Move.class.getDeclaredField("elementType");
				mElementType.setAccessible(true);
				
				mMoveType = Move.class.getDeclaredField("moveType");
				mMoveType.setAccessible(true);
				
				mTurnType = Move.class.getDeclaredField("turnType");
				mTurnType.setAccessible(true);
				
				mTargetSelectType = Move.class.getDeclaredField("targetSelectType");
				mTargetSelectType.setAccessible(true);
				
				mPriority = Move.class.getDeclaredField("priority");
				mPriority.setAccessible(true);
				
				mDelayAmount = Move.class.getDeclaredField("delayAmount");
				mDelayAmount.setAccessible(true);
				
				mActions = Move.class.getDeclaredField("actions");
				mActions.setAccessible(true);
				
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
				return;
			}
			
			ResultSet moveMeta;
			
			while(moves.next()) {
				
				Move m = new Move();
				
				try {
					
					mName.set(m, moves.getString("MoveName"));
					mDesc.set(m, moves.getString("Description"));
					
					mAccuracy.set(m, moves.getFloat("Accuracy"));
					mManaCost.set(m, moves.getInt("ManaCost"));
					mCritStage.set(m, moves.getInt("CritStage"));
					
					mPriority.set(m, moves.getInt("Priority"));
					mDelayAmount.set(m, moves.getInt("DelayAmount"));
					
					File actionDef = new File(ACTION_DEFINITIONS_DIRECTORY + "/" + moves.getString("ActionDefinition"));
					actionJson = "";
					try {
						BufferedReader reader = new BufferedReader(new FileReader(actionDef));
						String line;
						while((line = reader.readLine()) != null) {
							actionJson += line;
						}
						reader.close();
					} catch (IOException e) {
						System.err.println("Error loading move " + m.name + ". Unable to read action def.");
						e.printStackTrace();
						continue;
					}
					mActions.set(m, json.fromJson(MoveAction[].class, actionJson));
					
					moveMeta = GameData.executeQuery(
							"SELECT Moves.ID, ElementTypes.Type AS ElementType, MoveTypes.Type AS MoveType, TurnTypes.Type AS TurnType, TargetSelectTypes.Type AS TargetSelectType\n" + 
							"FROM Moves\n" + 
							"LEFT JOIN ElementTypes ON Moves.ElementType = ElementTypes.ID\n" + 
							"LEFT JOIN MoveTypes ON Moves.MoveType = MoveTypes.ID\n" + 
							"LEFT JOIN TurnTypes ON Moves.TurnType = TurnTypes.ID\n" + 
							"LEFT JOIN TargetSelectTypes ON Moves.TargetSelectType = TargetSelectTypes.ID\n" +
							"WHERE Moves.ID = '" + moves.getString("ID") + "';"
							);
					moveMeta.next();
					
					mElementType.set(m, ElementType.valueOf(moveMeta.getString("ElementType")));
					mMoveType.set(m, MoveType.valueOf(moveMeta.getString("MoveType")));
					mTurnType.set(m, TurnType.valueOf(moveMeta.getString("TurnType")));
					mTargetSelectType.set(m, MoveTargetSelectType.valueOf(moveMeta.getString("TargetSelectType")));
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					System.err.printf("Error assigning move fields! (for move %s)\n", moves.getString("MoveName"));
					e.printStackTrace();
					continue;
				}
				
				if(MOVE_DICTIONARY.containsKey(m.name)) {
					System.err.printf("[Error] Move ID clash for \'%s\'! Move with that name already exists.\n", m.name);
					continue;
				}
				
				boolean isInvalid = false;
				
				switch(m.targetSelectType) {
				
				case FRIENDLY_TEAM:
					for(MoveAction a : m.actions) {
						if(a instanceof MoveActionAilmentApplicator) {
							if(((MoveActionAilmentApplicator) a).target == MoveActionTarget.TARGET) {
								isInvalid = true;
							}
						}
						else if(a instanceof MoveActionDamage) {
							if(((MoveActionDamage) a).target == MoveActionTarget.TARGET) {
								isInvalid = true;
							}
						}
						else if(a instanceof MoveActionStatModifier) {
							if(((MoveActionStatModifier) a).target == MoveActionTarget.TARGET) {
								isInvalid = true;
							}
						}
						
						if(isInvalid) {
							System.err.printf(
									"Unable to add move %s: "
									+ "Invalid combination of FRIENDLY_TEAM TargetSelectType and "
									+ "MoveAction containing TARGET MoveActionTarget.\n", m.name);
						}
					}
					break;
					
				case NONE:
					for(MoveAction a : m.actions) {						
						if(a instanceof MoveActionAilmentApplicator) {
							if(((MoveActionAilmentApplicator) a).target == MoveActionTarget.TARGET) {
								isInvalid = true;
							}
						}
						else if(a instanceof MoveActionDamage) {
							if(((MoveActionDamage) a).target == MoveActionTarget.TARGET) {
								isInvalid = true;
							}
						}
						else if(a instanceof MoveActionStatModifier) {
							if(((MoveActionStatModifier) a).target == MoveActionTarget.TARGET) {
								isInvalid = true;
							}
						}
						
						if(isInvalid) {
							System.err.printf(
									"Unable to add move %s: "
									+ "Invalid combination of NONE TargetSelectType and "
									+ "MoveAction containing TARGET MoveActionTarget\n", m.name);
						}
					}
					break;
					
				case OPPONENT_TEAM:
					for(MoveAction a : m.actions) {						
						if(a instanceof MoveActionAilmentApplicator) {
							if(((MoveActionAilmentApplicator) a).target == MoveActionTarget.TARGET) {
								isInvalid = true;
							}
						}
						else if(a instanceof MoveActionDamage) {
							if(((MoveActionDamage) a).target == MoveActionTarget.TARGET) {
								isInvalid = true;
							}
						}
						else if(a instanceof MoveActionStatModifier) {
							if(((MoveActionStatModifier) a).target == MoveActionTarget.TARGET) {
								isInvalid = true;
							}
						}
						
						if(isInvalid) {
							System.err.printf(
									"Unable to add move %s: "
									+ "Invalid combination of OPPONENT_TEAM TargetSelectType and "
									+ "MoveAction containing TARGET MoveActionTarget\n", m.name);
						}
					}
					break;
					
				default:
					break;
				
				}
				
				if(!isInvalid) {
					MOVE_DICTIONARY.put(m.name, m);
					System.out.printf("Move \'%s\' loaded.\n", m.name);
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
