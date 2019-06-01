package com.ccode.alchemonsers.server;
import java.io.IOException;

import com.esotericsoftware.minlog.Log;

public class ServerStart {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Log.set(Log.LEVEL_NONE);
		
		AlchemonstersServer server = new AlchemonstersServer(31798);
		server.start();
		
	}
}
