package com.ccode.alchemonsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ccode.alchemonsters.engine.GameScreen;
import com.ccode.alchemonsters.engine.UI;

public class MainMenuScreen extends GameScreen {
	
	private Stage ui;
	private Table table;
	private Image titleHighlight;
	private float highlightHue = 0f;
	
	public MainMenuScreen(AlchemonstersGame game) {
		super(game);
	}
	
	@Override
	public void show() {
		//Build the UI
		ui = new Stage(game.uiView, game.batch);
		
		table = new Table();
		table.setFillParent(true);
		ui.addActor(table);
		
		TextureAtlas gameAtlas = game.assetManager.get("sprites_packed/packed.atlas");
		
		Image titleSplash = new Image(gameAtlas.findRegion("title_splash"));
		titleHighlight = new Image(gameAtlas.findRegion("title_splash_highlight"));
		Stack titleStack = new Stack(titleHighlight, titleSplash);
		
		TextButton startButton = new TextButton("Test Combat", UI.DEFAULT_SKIN);
		startButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new TestSoloCombatScreen(game));
			}
		});
		
		TextButton optionsButton = new TextButton("Test World", UI.DEFAULT_SKIN);
		optionsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new TestWorldScreen(game));
			}
		});
		
		TextButton exitButton = new TextButton("Exit", UI.DEFAULT_SKIN);
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				game.requestExit();
			}
		});
		
		table.add(titleStack);
		table.row();
		table.add(startButton).pad(10);
		table.row();
		table.add(optionsButton).pad(10);
		table.row();
		table.add(exitButton).pad(10);
		table.row();
		
		Gdx.input.setInputProcessor(ui);
	}

	@Override
	public void renderGraphics(float delta) {
		//no graphics for this screen
	}
	
	@Override
	public void renderUI(float delta) {
		highlightHue += delta * 35f;
		while(highlightHue > 360f) {
			highlightHue -= 360f;
		}
		titleHighlight.setColor(titleHighlight.getColor().fromHsv(highlightHue, 1f, 0.2f));
		
		ui.act(delta);
		ui.draw();
	}
	
	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		ui.dispose();
	}
}
