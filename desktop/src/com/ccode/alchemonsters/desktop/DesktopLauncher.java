package com.ccode.alchemonsters.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.ccode.alchemonsters.AlchemonstersGame;

public class DesktopLauncher {
	public static void main (String[] arg) throws Exception {
		
		//TODO: remove for release!
		//Run the texture packer
		Settings settings = new Settings();
		settings.maxHeight = 1024;
		settings.maxWidth = 1024;
		TexturePacker.process(settings, "../assets/sprites_unpacked", "../assets/sprites_packed", "packed");
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Alchemonsters";
		config.width = 1600;
		config.height = 900;
		config.addIcon("icon/icon128.png", Files.FileType.Internal);
		config.addIcon("icon/icon32.png", Files.FileType.Internal);
		config.addIcon("icon/icon16.png", Files.FileType.Internal);
		
		config.vSyncEnabled = false;
		config.foregroundFPS = 250;
		
		new LwjglApplication(new AlchemonstersGame(), config);
		
	}
}
