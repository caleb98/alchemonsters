package com.ccode.alchemonsters.server;

import java.io.IOException;

import com.ccode.alchemonsters.engine.event.EventManager;
import com.ccode.alchemonsters.net.KryoCreator;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class ServerLauncher {
	
	static Connection c;
	
	public static void main (String[] arg) throws IOException {
		
		//Create the server
		Server server = KryoCreator.createServer();
		server.addListener(new ServerListener());
		
		//Start the event manager thread
		EventManager.start();
		
		//Start the server thread
		Thread serverThread = new Thread(server, "ServerMainThread");
		serverThread.start();
		
	}
}
