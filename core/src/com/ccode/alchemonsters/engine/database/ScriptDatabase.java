package com.ccode.alchemonsters.engine.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.luaj.vm2.LuaValue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ccode.alchemonsters.engine.ScriptManager;

public class ScriptDatabase {
	
	private static HashMap<String, LuaValue> SCRIPT_DICTIONARY;
	private static boolean isInitialized = false;
	
	public static void initAndLoad() {
		SCRIPT_DICTIONARY = new HashMap<String, LuaValue>();
		
		try {
			ResultSet scripts = GameData.executeQuery("SELECT * FROM MoveScripts");
			
			while(scripts.next()) {
				
				String scriptName = scripts.getString("ScriptName");
				String scriptData = scripts.getString("ScriptData");
				
				if(SCRIPT_DICTIONARY.containsKey(scriptName)) {
					System.err.printf("[Error] Script ID clas for \'%s\'! Script with that name already exists.", scriptName);
					continue;
				}
				
				String fullScript = String.format("function activate(move, context, source, sourceTeam, target, targetTeam)\n%s\nend", scriptData);
				ScriptManager.GLOBAL_CONTEXT.load(fullScript).call();
				LuaValue scriptFunction = ScriptManager.GLOBAL_CONTEXT.get("activate");
				
				SCRIPT_DICTIONARY.put(scriptName, scriptFunction);
				System.out.printf("Script \'%s\' loaded.\n", scriptName);
				
			}
		} catch (SQLException e) {
			System.err.println("Error loading scripts!");
			e.printStackTrace();
			System.exit(-1);
		}
		
		isInitialized = true;
	}
	
	public static LuaValue getScript(String name) {
		if(!isInitialized) {
			throw new IllegalStateException("Attemted to retreive script before initializing the database.");
		}
		
		return SCRIPT_DICTIONARY.get(name);
	}
	
}
