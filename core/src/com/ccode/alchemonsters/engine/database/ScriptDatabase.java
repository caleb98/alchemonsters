package com.ccode.alchemonsters.engine.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.luaj.vm2.LuaValue;

import com.ccode.alchemonsters.engine.ScriptManager;

public class ScriptDatabase {
	
	private static HashMap<String, LuaValue> SCRIPT_DICTIONARY;
	private static boolean isInitialized = false;
	
	public static void initAndLoad() {
		SCRIPT_DICTIONARY = new HashMap<String, LuaValue>();
		
		File scriptsDir = new File("scripts");
		File[] scripts = scriptsDir.listFiles((f)->{
			return f.getName().endsWith(".lua");
		});
		
		String scriptName;
		String scriptData;
		for(File script : scripts) {
			
			scriptName = script.getName().replace(".lua", "");
			
			if(SCRIPT_DICTIONARY.containsKey(scriptName)) {
				System.err.printf("[Error] Script ID clas for \'%s\'! Script with that name already exists.", scriptName);
				continue;
			}
			
			scriptData = "";
			try {
				BufferedReader reader = new BufferedReader(new FileReader(script));
				String line;
				while((line = reader.readLine()) != null) {
					scriptData += line + "\n";
				}
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			String fullScript = String.format("function activate(moveInstance, sourceTeam, opponentTeam)\n%s\nend", scriptData);
			ScriptManager.GLOBAL_CONTEXT.load(fullScript).call();
			LuaValue scriptFunction = ScriptManager.GLOBAL_CONTEXT.get("activate");
			
			SCRIPT_DICTIONARY.put(scriptName, scriptFunction);
			System.out.printf("Script \'%s\' loaded.\n", scriptName);
			
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
