package com.ccode.alchemonsers.server;

public class ActionCreateGameRoom {

	public String roomName;
	public String password;
	
	public ActionCreateGameRoom() {}
	
	public ActionCreateGameRoom(String roomID, String password) {
		this.roomName = roomID;
		this.password = password;
	}
	
}
