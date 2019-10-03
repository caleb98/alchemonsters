package com.ccode.alchemonsters.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ccode.alchemonsters.AlchemonstersGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Alchemonsters";
		config.width = 1600;
		config.height = 900;
		config.addIcon("data/sprites/icon128.png", Files.FileType.Internal);
		config.addIcon("data/sprites/icon32.png", Files.FileType.Internal);
		config.addIcon("data/sprites/icon16.png", Files.FileType.Internal);
		
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		
		new LwjglApplication(new AlchemonstersGame(), config);
	}
}
