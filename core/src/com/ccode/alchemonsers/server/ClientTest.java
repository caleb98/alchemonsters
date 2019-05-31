package com.ccode.alchemonsers.server;

import java.io.IOException;
import java.util.HashMap;

import com.ccode.alchemonsters.online.KryoRegistration;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientTest {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		for(int i = 0 ; i < 10; ++i) {
			Client client = new Client();
			KryoRegistration.registerKyroClasses(client);
			client.start();
			client.connect(5000, "localhost", 31798);
			client.sendTCP(new ActionCreateGameRoom("Room " + i, ""));
			client.close();
		}
		
		Client client = new Client();
		KryoRegistration.registerKyroClasses(client);
		
		client.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if(object instanceof HashMap) {
					HashMap<String, RoomInfo> map = (HashMap<String, RoomInfo>) object;
					
					for(String s : map.keySet()) {
						RoomInfo i = map.get(s);
						System.out.printf("%s\t%s\t%s\n", i.roomID, s, i.isPrivate ? "private" : "public");
					}
				}
			}
		});
		
		client.start();
		client.connect(5000, "localhost", 31798);
		client.sendTCP(new ActionRequestRoomList());
		
		Thread.sleep(10000);
		
	}

}
