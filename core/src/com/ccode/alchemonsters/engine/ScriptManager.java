package com.ccode.alchemonsters.engine;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.ccode.alchemonsters.combat.moves.MoveActionDamage;

public class ScriptManager {

	public static Globals GLOBAL_CONTEXT;
	
	private static MoveActionDamage LUA_DAMAGE_INSTANCE;
	
	public static void init() {
		GLOBAL_CONTEXT = JsePlatform.standardGlobals();
		GLOBAL_CONTEXT.load(
				"_MCombatDamageDealt = 'com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt'\n"
			  + "_MCombatHealingReceived = 'com.ccode.alchemonsters.engine.event.messages.MCombatHealingReceived'\n"
			  + "_MCombatAilmentApplied = 'com.ccode.alchemonsters.engine.event.messages.MCombatAilmentApplied'\n"
			  + "_MCombatStatBuffApplied = 'com.ccode.alchemonsters.engine.event.messages.MCombatStatBuffApplied'\n"
		).call();
		
		//Expose a Damage action instance to allow scripts to calc damage and publish combat events
		LUA_DAMAGE_INSTANCE = new MoveActionDamage();
		GLOBAL_CONTEXT.set("Damage", CoerceJavaToLua.coerce(LUA_DAMAGE_INSTANCE));
	}
	
}
