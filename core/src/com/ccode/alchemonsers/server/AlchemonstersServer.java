package com.ccode.alchemonsers.server;

import java.io.IOException;
import java.util.HashMap;

import com.ccode.alchemonsters.online.KryoRegistration;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class AlchemonstersServer extends Thread {
	
	private Server server;
	private HashMap<Integer, GameRoom> openRooms;
	private HashMap<String, RoomInfo> roomInfo;
	private HashMap<Integer, GameRoom> launchedRooms;
	
	public AlchemonstersServer(int port) throws IOException {
		openRooms = new HashMap<Integer, GameRoom>();
		roomInfo = new HashMap<String, RoomInfo>();
		
		server = new Server();
		server.bind(port);
		
		KryoRegistration.registerKyroClasses(server);
		
		server.addListener(new Listener() {
			
			@Override
			public void connected(Connection connection) {
				
			}
			
			@Override
			public void disconnected(Connection connection) {
				
			}
			
			@Override
			public void received(Connection connection, Object object) {
				if(object instanceof ActionRequestRoomList) {
					
					connection.sendTCP(roomInfo);
					
				}
				if(object instanceof ActionCreateGameRoom) {
					
					ActionCreateGameRoom a = (ActionCreateGameRoom) object;
					
					//Create the Game Room
					GameRoom room = new GameRoom(a.password);
					room.addPlayer(connection);
					
					//Create corresponding info
					RoomInfo info = new RoomInfo(a.roomName, !a.password.equals(""), room.roomID);
					
					//Add room and info to maps
					openRooms.put(room.roomID, room);
					roomInfo.put(info.name, info);
					
					System.out.printf("Created new %s room \'%s\' (%s)\n", info.isPrivate ? "private" : "public", info.name, room.roomID);
					
				}
				if(object instanceof ActionJoinGameRoom) {
					
					ActionJoinGameRoom a = (ActionJoinGameRoom) object;
					
					if(openRooms.containsKey(a.roomID)) {
						GameRoom room = openRooms.get(a.roomID);
						room.addPlayer(connection);
						
						//TODO: game started stuff?
					}
					else {
						//TODO: no room found
					}
					
				}
			}
			
		});
	}
	
	@Override
	public void run() {
		try {			
			while(true) {
				server.update(5000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
