package com.ccode.alchemonsers.server;

import com.esotericsoftware.kryonet.Connection;

public class GameRoom {

	private static int ROOM_COUNTER = 0;
	
	public String password;
	public int roomID;
	public Connection playerOneConnection;
	public Connection playerTwoConnection;

	public GameRoom(String password) {
		this.password = password;
		
		roomID = ROOM_COUNTER++;
		if(ROOM_COUNTER > 100_000) {
			ROOM_COUNTER = 0;
		}
	}
	
	public void addPlayer(Connection connection) {
		if(playerOneConnection == null) {
			playerOneConnection = connection;
		}
		else if(playerTwoConnection == null) {
			playerTwoConnection = connection;
			startGame();
		}
	}
	
	public void startGame() {
		
	}
	
	public boolean isFilled() {
		return playerOneConnection != null && playerTwoConnection != null;
	}
	
}
