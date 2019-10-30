package com.ccode.alchemonsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
	
	private AlchemonstersGame game;
	
	private Stage ui;
	private Table table;
	private Image titleHighlight;
	private float highlightHue = 0f;
	
	public MainMenuScreen(AlchemonstersGame game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		//Build the UI
		ui = new Stage(new ScreenViewport(), game.batch);
		
		table = new Table();
		table.setFillParent(true);
		ui.addActor(table);
		
		TextureAtlas gameAtlas = game.assetManager.get("sprites_packed/packed.atlas");
		
		Image titleSplash = new Image(gameAtlas.findRegion("title_splash"));
		titleHighlight = new Image(gameAtlas.findRegion("title_splash_highlight"));
		Stack titleStack = new Stack(titleHighlight, titleSplash);
		TextButton startButton = new TextButton("Play", UI.DEFAULT_SKIN);
		startButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				game.setScreen(new TestCombatScreen(game));
			}
		});
		TextButton optionsButton = new TextButton("Options", UI.DEFAULT_SKIN);
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
	public void render(float delta) {
		
		highlightHue += delta * 35f;
		while(highlightHue > 360f) {
			highlightHue -= 360f;
		}
		titleHighlight.setColor(titleHighlight.getColor().fromHsv(highlightHue, 1f, 0.2f));
		
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		ui.act(delta);
		ui.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		ui.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
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
