package com.ccode.alchemonsters.engine.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.utils.Json;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.combat.moves.MoveAction;
import com.ccode.alchemonsters.combat.moves.MoveActionAilmentApplicator;
import com.ccode.alchemonsters.combat.moves.MoveActionAilmentRemoval;
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
	
	private static HashMap<String, Move> MOVE_DICTIONARY;
	private static boolean isInitialized = false;
	
	public static void initAndLoad() {
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
			
			ResultSet moves = GameData.executeQuery("SELECT * FROM Moves");
			String actionJSON;
			
			while(moves.next()) {
				
				Move m = new Move();
				
				m.name = moves.getString("MoveName");
				m.desc = moves.getString("Description");
				m.accuracy = moves.getFloat("Accuracy");
				m.manaCost = moves.getInt("ManaCost");
				m.critStage = moves.getInt("CritStage");
				m.elementType = ElementType.valueOf(moves.getString("ElementType"));
				m.moveType = MoveType.valueOf(moves.getString("MoveType"));
				m.targetSelectType = MoveTargetSelectType.valueOf(moves.getString("TargetSelectType"));
				
				m.priority = moves.getInt("Priority");
				m.turnType = TurnType.valueOf(moves.getString("TurnType"));
				m.delayAmount = moves.getInt("DelayAmount");
				
				actionJSON = moves.getString("ActionData");
				m.actions = json.fromJson(MoveAction[].class, actionJSON);
				
				if(MOVE_DICTIONARY.containsKey(m.name)) {
					System.err.printf("[Error] Move ID clash for \'%s\'! Move with that name already exists.", m.name);
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
						else if(a instanceof MoveActionAilmentRemoval) {
							if(((MoveActionAilmentRemoval) a).target == MoveActionTarget.TARGET) {
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
						else if(a instanceof MoveActionAilmentRemoval) {
							if(((MoveActionAilmentRemoval) a).target == MoveActionTarget.TARGET) {
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
						else if(a instanceof MoveActionAilmentRemoval) {
							if(((MoveActionAilmentRemoval) a).target == MoveActionTarget.TARGET) {
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
