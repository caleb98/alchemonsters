package com.ccode.alchemonsters.engine.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GameData {

	private static final String GAME_DATA_DIR = "gamedata.db";
	
	private static boolean isInitialized = false;
	private static Connection connection;
	
	public static void init() {		
		try {			
			String dbURL = "jdbc:sqlite:" + GAME_DATA_DIR;
			connection = DriverManager.getConnection(dbURL);
		} catch (SQLException e) {
			System.err.println("Unable to load game data database.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		isInitialized = true;
	}
	
	public static ResultSet executeQuery(String sql) throws SQLException {
		if(!isInitialized) {
			throw new IllegalStateException("Attempted to execute SQL query without loading database.");
		}
		Statement statement = connection.createStatement();
		return statement.executeQuery(sql);
	}
	
}
