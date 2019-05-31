package com.ccode.alchemonsers.server;
import java.io.IOException;

public class ServerStart {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		AlchemonstersServer server = new AlchemonstersServer(31798);
		server.start();
		
		
	}
}
