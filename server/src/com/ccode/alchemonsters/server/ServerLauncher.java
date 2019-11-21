package com.ccode.alchemonsters.server;

import java.io.IOException;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ccode.alchemonsters.engine.ScriptManager;
import com.ccode.alchemonsters.engine.database.CreatureDatabase;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.engine.database.ScriptDatabase;
import com.ccode.alchemonsters.engine.database.StatusAilmentDatabase;
import com.ccode.alchemonsters.engine.event.EventManager;
import com.ccode.alchemonsters.net.KryoCreator;
import com.esotericsoftware.kryonet.Server;

public class ServerLauncher implements ApplicationListener {
	
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 0;
		config.height = 0;
		
		LwjglApplication app = new LwjglApplication(new ServerLauncher());
		
	}

	@Override
	public void create() {
		
		//Init systems
		ScriptManager.init();
		ScriptDatabase.initAndLoad();
		CreatureDatabase.initAndLoad();
		MoveDatabase.initAndLoad();
		StatusAilmentDatabase.init();
		
		//Create the server
		Server server = KryoCreator.createServer();
		try {
			server.bind(48372);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		server.addListener(new VersusServer());
		
		//Start the server thread
		Thread serverThread = new Thread(server, "ServerMainThread");
		serverThread.start();
		
	}

	public void render() {}
	
	public void resize(int width, int height) {}
	public void pause() {}
	public void resume() {}
	public void dispose() {}
	
}
