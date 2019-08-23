package com.ccode.alchemonsters.engine;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

public class ScriptManager {

	public static Globals GLOBAL_CONTEXT;
	
	public static void init() {
		GLOBAL_CONTEXT = JsePlatform.standardGlobals();
	}
	
}
