package com.ccode.alchemonsters.server;

import java.io.IOException;

import com.ccode.alchemonsters.net.KryoCreator;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class ServerLauncher {
	
	static Connection c;
	
	public static void main (String[] arg) throws IOException {
		
		Server server = KryoCreator.createServer();
		
		
	}
}
