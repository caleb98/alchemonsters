package com.ccode.alchemonsters.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class UI {

	public static Skin DEFAULT_SKIN;
	
	public static BitmapFont TITLE_FONT_SMALL;
	public static BitmapFont TITLE_FONT_MEDIUM;
	public static BitmapFont TITLE_FONT_LARGE;
	
	public static BitmapFont TEXT_FONT_SMALL;
	public static BitmapFont TEXT_FONT_MEDIUM;
	public static BitmapFont TEXT_FONT_LARGE;
	
	private static boolean isInitialized = false;
	
	public static void initAndLoad() {
		DEFAULT_SKIN = new Skin(Gdx.files.internal("ui/ui.json"));
		isInitialized = true;
	}
	
	public static boolean isInitialized() {
		return isInitialized;
	}
	
	public static void dispose() {
		DEFAULT_SKIN.dispose();
	}
	
}
