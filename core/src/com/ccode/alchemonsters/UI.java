package com.ccode.alchemonsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class UI {

	public static Skin DEFAULT_SKIN;
	
	public static BitmapFont DEFAULT_FONT_SMALL;
	public static BitmapFont DEFAULT_FONT_MED;
	public static BitmapFont DEFAULT_FONT_LARGE;
	
	private static boolean isInitialized = false;
	
	public static void initAndLoad() {
		
		//Load fonts
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("ui/default/bulkypix.ttf"));
		FreeTypeFontParameter p = new FreeTypeFontParameter();
		p.color = Color.BLACK;
		
		p.size = 20;
		DEFAULT_FONT_SMALL = gen.generateFont(p);
		p.size = 28;
		DEFAULT_FONT_MED = gen.generateFont(p);
		p.size = 36;
		DEFAULT_FONT_LARGE = gen.generateFont(p);
		gen.dispose();
		
		//TODO: include fonts in the ui itself
		//Load and setup default UI skin
		DEFAULT_SKIN = new Skin(Gdx.files.internal("ui/temp/uiskin.json"));
//		Window.WindowStyle ws = DEFAULT_SKIN.get(Window.WindowStyle.class);
//		ws.titleFont = DEFAULT_FONT_MED;
//		TextButton.TextButtonStyle tbs = DEFAULT_SKIN.get(TextButton.TextButtonStyle.class);
//		tbs.font = DEFAULT_FONT_SMALL;
//		tbs.overFontColor = Color.WHITE;
//		
//		//TODO: include this in skin
//		Label.LabelStyle ls = new Label.LabelStyle(DEFAULT_FONT_SMALL, Color.BLACK);
//		DEFAULT_SKIN.add("default", ls);
		
		isInitialized = true;
	}
	
	public static boolean isInitialized() {
		return isInitialized;
	}
	
	public static void dispose() {
		DEFAULT_SKIN.dispose();
		DEFAULT_FONT_SMALL.dispose();
		DEFAULT_FONT_MED.dispose();
		DEFAULT_FONT_LARGE.dispose();
	}
	
}
