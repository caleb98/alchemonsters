package com.ccode.alchemonsers.server;

public class ActionJoinGameRoom {

	public int roomID;
	public String password;
	
	public ActionJoinGameRoom() {}
	
	public ActionJoinGameRoom(int roomID, String password) {
		this.roomID = roomID;
		this.password = password;
	}
	
}
