package com.ccode.alchemonsters.server;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class ServerLauncher {
	
	static Connection c;
	
	public static void main (String[] arg) throws IOException {
		
		Server server = new Server();	
		server.bind(48371, 48371);
		server.addListener(new Listener(){
			@Override
			public void connected(Connection connection) {
				System.out.printf("%s connected.\n", connection.getRemoteAddressUDP());
				c = connection;
			}
			
			@Override
			public void disconnected(Connection connection) {
				if(c == connection) {
					System.out.println("yoo");
				}
				System.out.printf("%s disconnected.\n", connection.getRemoteAddressUDP());
				System.out.println("Exiting...");
				System.exit(0);
			}
		});
		
		server.start();
		
	}
}
