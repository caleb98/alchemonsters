package com.ccode.alchemonsters.engine.database;

import java.util.HashMap;

import org.luaj.vm2.LuaValue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ccode.alchemonsters.engine.ScriptManager;

public class ScriptDatabase {

	private static final String SCRIPT_DIRECTORY = "moves";
	
	private static HashMap<String, LuaValue> SCRIPT_DICTIONARY;
	private static boolean isInitialized = false;
	
	public static void initAndLoad() {
		SCRIPT_DICTIONARY = new HashMap<String, LuaValue>();
		
		FileHandle loadDir = Gdx.files.internal(SCRIPT_DIRECTORY);
		for(FileHandle luaFile : loadDir.list("lua")) {
			try {
				if(SCRIPT_DICTIONARY.containsKey(luaFile.nameWithoutExtension())) {
					System.err.printf("[Error] Script ID clas for \'%s\'! Script with that name already exists.", luaFile.nameWithoutExtension());
					continue;
				}
				
				String fullScript = String.format("function activate(move, context, source, sourceTeam, target, targetTeam)\n%s\nend", luaFile.readString());
				ScriptManager.GLOBAL_CONTEXT.load(fullScript).call();
				LuaValue scriptFunction = ScriptManager.GLOBAL_CONTEXT.get("activate");
				
				SCRIPT_DICTIONARY.put(luaFile.nameWithoutExtension(), scriptFunction);
				System.out.printf("Script \'%s\' loaded.\n", luaFile.nameWithoutExtension());
			} catch (GdxRuntimeException e) {
				System.out.printf("Error loading script file: %s", luaFile.file().getName());
				System.out.println(e.getMessage());
			}
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
