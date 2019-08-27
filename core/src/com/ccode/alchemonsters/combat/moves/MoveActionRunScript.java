package com.ccode.alchemonsters.combat.moves;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.engine.ScriptManager;

public class MoveActionRunScript implements MoveAction, Serializable {

	public LuaValue scriptFunction;

	@Override
	public void activate(Move move, BattleContext context, Creature source, CreatureTeam sourceTeam, Creature target, CreatureTeam targetTeam) {
		try {
			scriptFunction.invoke(new LuaValue[] {
				CoerceJavaToLua.coerce(move), 
				CoerceJavaToLua.coerce(context), 
				CoerceJavaToLua.coerce(source), 
				CoerceJavaToLua.coerce(sourceTeam), 
				CoerceJavaToLua.coerce(target), 
				CoerceJavaToLua.coerce(targetTeam)
			});
		} catch (LuaError le) {
			System.err.println("Scripted move action encountered a Lua Error! Details:");
			le.printStackTrace(System.err);
		}
	}

	@Override
	public void write(Json json) {
		//TODO this probably will never be called, but something should be done here
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		String fullScript = "";
		if(jsonData.has("scriptFile")) {
			String localFilePath = "data/moves/" + jsonData.getString("scriptFile");
			FileHandle scriptFile = Gdx.files.internal(localFilePath);
			try {
				fullScript = String.format("function activate(move, context, source, sourceTeam, target, targetTeam)\n%s\nend", scriptFile.readString());
			} catch (GdxRuntimeException gde) {
				System.out.println(gde.getMessage());
			}
		}
		else {
			fullScript = String.format("function activate(move, context, source, sourceTeam, target, targetTeam)\n%s\nend", jsonData.getString("script"));
		}
		ScriptManager.GLOBAL_CONTEXT.load(fullScript).call();
		scriptFunction = ScriptManager.GLOBAL_CONTEXT.get("activate");
	}
	
	
	
}
