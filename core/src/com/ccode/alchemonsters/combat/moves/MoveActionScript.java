package com.ccode.alchemonsters.combat.moves;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.engine.database.ScriptDatabase;

public class MoveActionScript implements MoveAction {

	public String scriptName;

	@Override
	public void activate(MoveInstance moveInstance, BattleTeam sourceTeam, BattleTeam opponentTeam) {
		try {			
			ScriptDatabase.getScript(scriptName).invoke(new LuaValue[] {
				CoerceJavaToLua.coerce(moveInstance),
				CoerceJavaToLua.coerce(sourceTeam),
				CoerceJavaToLua.coerce(opponentTeam)
			});
		} catch (LuaError le) {
			System.err.println("Scripted move action encountered a Lua Error! Details:");
			le.printStackTrace(System.err);
		}
	}
	
}
