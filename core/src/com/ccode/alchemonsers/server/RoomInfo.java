package com.ccode.alchemonsers.server;

public class RoomInfo {
	
	public String name;
	public boolean isPrivate;
	public int roomID;
	
	public RoomInfo() {}
	
	public RoomInfo(String name, boolean isPrivate, int roomID) {
		this.name = name;
		this.isPrivate = isPrivate;
		this.roomID = roomID;
	}
	
}
