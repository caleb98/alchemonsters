package com.ccode.alchemonsters.engine.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import com.ccode.alchemonsters.creature.CreatureBase;
import com.ccode.alchemonsters.creature.ElementType;

public class CreatureDatabase {
	
	private static HashMap<String, CreatureBase> CREATURE_DICTIONARY;
	private static boolean isInitialized = false;
	
	public static void init() {		
		CREATURE_DICTIONARY = new HashMap<String, CreatureBase>();
		
		try {
			ResultSet creaturesTable = GameData.executeQuery("SELECT * FROM CreatureBases");
			ResultSet elementTypes;
			ResultSetMetaData meta;
			
			while(creaturesTable.next()) {
				CreatureBase base = new CreatureBase();
				
				base.name = creaturesTable.getString("CreatureName");
				base.desc = creaturesTable.getString("Description");
				
				base.minBaseHealth = creaturesTable.getInt("MinBaseHealth");
				base.maxBaseHealth = creaturesTable.getInt("MaxBaseHealth");
				
				base.minBaseMana = creaturesTable.getInt("MinBaseMana");
				base.maxBaseMana = creaturesTable.getInt("MaxBaseMana");
				
				base.baseVitae = creaturesTable.getInt("BaseVitae");
				base.baseFocus = creaturesTable.getInt("BaseFocus");
				base.baseMagicAtk = creaturesTable.getInt("BaseMagicAttack");
				base.baseMagicDef = creaturesTable.getInt("BaseMagicDefense");
				base.basePhysAtk = creaturesTable.getInt("BasePhysicalAttack");
				base.basePhysDef = creaturesTable.getInt("BasePhysicalDefense");
				base.basePenetration = creaturesTable.getInt("BasePenetration");
				base.baseResistance = creaturesTable.getInt("BaseResistance");
				base.baseSpeed = creaturesTable.getInt("BaseSpeed");
				
				elementTypes = GameData.executeQuery(
						"SELECT CreatureBases.CreatureName AS Name, TypeOne.Type AS TypeOne, TypeTwo.Type AS TypeTwo\n" +
						"FROM CreatureBases\n" +
						"LEFT JOIN ElementTypes AS TypeOne ON CreatureBases.TypeOne = TypeOne.ID\n" +
						"LEFT JOIN ElementTypes AS TypeTwo ON CreatureBases.TypeTwo = TypeTwo.ID\n" + 
						"WHERE Name = '" + base.name + "';"
						);

				int total = 0;
				while(elementTypes.next()) {
					if(total > 0) {
						System.err.printf(
								"Warning: CreatureID %s has two registered type sets.\n", 
								creaturesTable.getInt("ID"));
						break;
					}
					
					base.types = new ElementType[]{
							ElementType.valueOf(elementTypes.getString("TypeOne")),
							elementTypes.getString("TypeTwo") == null ? null : ElementType.valueOf(elementTypes.getString("TypeTwo"))
					};
					total++;
				}
				
				if(CREATURE_DICTIONARY.containsKey(base.name)) {
					System.err.printf("Error: Creature with name %s already present!", base.name);
				}
				else {
					System.out.printf("Loaded creature base %s.\n", base.name);
					CREATURE_DICTIONARY.put(base.name, base);
				}
			}
			
			isInitialized = true;
		} catch (SQLException e) {
			System.err.println("Unable to fetch creature database table.");
			e.printStackTrace();
			System.exit(-1);
		}
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
