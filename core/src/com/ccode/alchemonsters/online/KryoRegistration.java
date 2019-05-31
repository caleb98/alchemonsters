package com.ccode.alchemonsters.online;

import java.util.HashMap;

import com.ccode.alchemonsers.server.ActionCreateGameRoom;
import com.ccode.alchemonsers.server.ActionJoinGameRoom;
import com.ccode.alchemonsers.server.ActionRequestRoomList;
import com.ccode.alchemonsers.server.RoomInfo;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class KryoRegistration {

	public static void registerKyroClasses(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		
		//TODO: register all network classes here
		kryo.register(ActionRequestRoomList.class);
		kryo.register(ActionCreateGameRoom.class);
		kryo.register(ActionJoinGameRoom.class);
		kryo.register(RoomInfo.class);
		kryo.register(HashMap.class);
	 }
	
}
