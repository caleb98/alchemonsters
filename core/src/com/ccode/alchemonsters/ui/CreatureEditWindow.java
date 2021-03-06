package com.ccode.alchemonsters.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.CreatureBase;
import com.ccode.alchemonsters.creature.CreatureFactory;
import com.ccode.alchemonsters.creature.CreatureNature;
import com.ccode.alchemonsters.creature.StatType;
import com.ccode.alchemonsters.engine.UI;
import com.ccode.alchemonsters.engine.database.CreatureDatabase;
import com.ccode.alchemonsters.engine.database.MoveDatabase;
import com.ccode.alchemonsters.util.GameRandom;

public class CreatureEditWindow extends Window {
	
	private SelectBox<String> creatureSelectBox;
	private TextField creatureNameEdit;
	private Slider levelSlider;
	private Label levelDisplay;
	private Slider baseHealthSlider;
	private Label baseHealthDisplay;
	private Slider baseManaSlider;
	private Label baseManaDisplay;
	
	private TextField vitaeEdit;
	private TextField focusEdit;
	private TextField magicAtkEdit;
	private TextField magicDefEdit;
	private TextField physAtkEdit;
	private TextField physDefEdit;
	private TextField penetrationEdit;
	private TextField resistanceEdit;
	private TextField speedEdit;
	
	private SelectBox<StatType> positiveNature;
	private SelectBox<StatType> negativeNature;
	
	private List<String> movesActiveList;
	//Moves Selection Window
	private Window moveSelectWindow;
	private List<String> movesAvailableList;
	
	private TextButton acceptButton;
	
	//Creature generated from last click of accept button
	private Creature editedCreature;
	private boolean isEditComplete = false;
	
	public CreatureEditWindow(Stage ui) {
		super("Edit Creature", UI.DEFAULT_SKIN);
		
		Label creatureSelectText = new Label("Creature:", UI.DEFAULT_SKIN);
		creatureSelectBox = new SelectBox<>(UI.DEFAULT_SKIN);
		Array<String> items = new Array<>();
		for(String id : CreatureDatabase.getAvailableCreatureIDs()) {
			items.add(id);
		}
		items.sort();
		creatureSelectBox.setItems(items);
		creatureSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				CreatureBase b = CreatureDatabase.getBase(creatureSelectBox.getSelected());
				baseHealthSlider.setRange(b.minBaseHealth, b.maxBaseHealth);
				baseManaSlider.setRange(b.minBaseMana, b.maxBaseMana);
			}
		});
		
		Label creatureNameText = new Label("Name: ", UI.DEFAULT_SKIN);
		creatureNameEdit = new TextField("", UI.DEFAULT_SKIN);
		
		add(creatureSelectText, creatureSelectBox, creatureNameText, creatureNameEdit);
		row();
		
		Label levelLabel = new Label("Level", UI.DEFAULT_SKIN);
		levelSlider = new Slider(1, 100, 1, false, UI.DEFAULT_SKIN);
		levelDisplay = new Label("", UI.DEFAULT_SKIN) {
			@Override
			public void act(float delta) {
				super.act(delta);
				setText((int) levelSlider.getValue());
			}
		};
		
		add(levelLabel, levelSlider, levelDisplay);
		row();
		
		Label baseHealthLabel = new Label("Base Health", UI.DEFAULT_SKIN);
		baseHealthSlider = new Slider(0, 10, 1, false, UI.DEFAULT_SKIN);
		baseHealthDisplay = new Label("", UI.DEFAULT_SKIN) {
			@Override
			public void act(float delta) {
				super.act(delta);
				setText((int) baseHealthSlider.getValue());
			}
		};
		
		add(baseHealthLabel, baseHealthSlider, baseHealthDisplay);
		row();
		
		Label baseManaLabel = new Label("Base Mana", UI.DEFAULT_SKIN);
		baseManaSlider = new Slider(0, 10, 1, false, UI.DEFAULT_SKIN);
		baseManaDisplay = new Label("", UI.DEFAULT_SKIN) {
			@Override
			public void act(float delta) {
				super.act(delta);
				setText((int) baseManaSlider.getValue());
			}
		};
		
		add(baseManaLabel, baseManaSlider, baseManaDisplay);
		row();
		
		add(new Label("", UI.DEFAULT_SKIN));
		row();
		add(new Label("Attunement Values (=IVs)", UI.DEFAULT_SKIN));
		row();
		
		Label vitaeLabel = new Label("Vitae: ", UI.DEFAULT_SKIN);
		vitaeEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label focusLabel = new Label("Focus: ", UI.DEFAULT_SKIN);
		focusEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label magicAtkLabel = new Label("Magic Atk: ", UI.DEFAULT_SKIN);
		magicAtkEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label magicDefLabel = new Label("Magic Def: ", UI.DEFAULT_SKIN);
		magicDefEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label physAtkLabel = new Label("Phys Atk: ", UI.DEFAULT_SKIN);
		physAtkEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label physDefLabel = new Label("Phys Def: ", UI.DEFAULT_SKIN);
		physDefEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label penetrationLabel = new Label("Penetration: ", UI.DEFAULT_SKIN);
		penetrationEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label resistanceLabel = new Label("Resistance: ", UI.DEFAULT_SKIN);
		resistanceEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label speedLabel = new Label("Speed: ", UI.DEFAULT_SKIN);
		speedEdit = new TextField("16", UI.DEFAULT_SKIN);
		
		add(vitaeLabel, vitaeEdit);
		add(focusLabel, focusEdit);
		row();
		add(magicAtkLabel, magicAtkEdit);
		add(physDefLabel, physDefEdit);
		row();
		add(magicDefLabel, magicDefEdit);
		add(penetrationLabel, penetrationEdit);
		row();
		add(physAtkLabel, physAtkEdit);
		add(resistanceLabel, resistanceEdit);
		row();
		add(speedLabel, speedEdit);
		row();
		
		Label movesLabel = new Label("Available Moves", UI.DEFAULT_SKIN);
		movesActiveList = new List<>(UI.DEFAULT_SKIN);
		movesActiveList.setItems("Move A", "Move B", "Move C", "Move D", "Move E", "Move F", "Move G");
		ScrollPane movesPane = new ScrollPane(movesActiveList, UI.DEFAULT_SKIN);
		movesPane.setScrollbarsVisible(true);
		movesPane.setFadeScrollBars(false);
		
		Label positiveNatureLabel = new Label("Positive Nature Stat: ", UI.DEFAULT_SKIN);
		positiveNature = new SelectBox<>(UI.DEFAULT_SKIN);
		positiveNature.setItems(StatType.values());
		Label negativeNatureLabel = new Label("Negative Nature Stat: ", UI.DEFAULT_SKIN);
		negativeNature = new SelectBox<>(UI.DEFAULT_SKIN);
		negativeNature.setItems(StatType.values());

		add(new Label("", UI.DEFAULT_SKIN));
		row();
		add(new Label("Nature Selection", UI.DEFAULT_SKIN));
		row();
		add(positiveNatureLabel, positiveNature);
		row();
		add(negativeNatureLabel, negativeNature);
		row();
		add(new Label("", UI.DEFAULT_SKIN));
		row();
		
		Table moveButtons = new Table(UI.DEFAULT_SKIN);
		TextButton removeButton = new TextButton("Remove", UI.DEFAULT_SKIN);
		removeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(movesActiveList.getSelectedIndex() != -1) {
					Array<String> items = movesActiveList.getItems();
					items.removeIndex(movesActiveList.getSelectedIndex());
					movesActiveList.setItems(items);
					movesPane.invalidate();
					movesActiveList.invalidate();
				}
			}
		});
		TextButton addButton = new TextButton("Add", UI.DEFAULT_SKIN);
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				moveSelectWindow.setVisible(true);
				moveSelectWindow.toFront();
				movesPane.invalidate();
				movesActiveList.invalidate();
			}
		});
		TextButton clearButton = new TextButton("Clear", UI.DEFAULT_SKIN);
		clearButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				movesActiveList.clearItems();
				movesPane.invalidate();
				movesActiveList.invalidate();
			}
		});
		TextButton randomButton = new TextButton("Random", UI.DEFAULT_SKIN);
		randomButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String[] allMoves = MoveDatabase.getLoadedMoveNames().toArray(new String[]{});
				if(movesActiveList.getItems().size >= allMoves.length) {
					return;
				}
				String added = "";
				do {
					added = allMoves[GameRandom.nextInt(allMoves.length)];
				} while(movesActiveList.getItems().contains(added, false));
				movesActiveList.getItems().add(added);
				movesPane.invalidate();
				movesActiveList.invalidate();
			}
		});
		moveButtons.add(clearButton);
		moveButtons.row();
		moveButtons.add(removeButton);
		moveButtons.row();
		moveButtons.add(addButton);
		moveButtons.row();
		moveButtons.add(randomButton);
		
		add(movesLabel);
		row();
		add(movesPane).fill().expand();
		add(moveButtons);
		row();
		
		TextButton editCancel = new TextButton("Cancel", UI.DEFAULT_SKIN);
		editCancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
			}
		});
		
		acceptButton = new TextButton("Accept", UI.DEFAULT_SKIN);
		acceptButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
					Creature edit = new Creature();
					
					edit.base = CreatureDatabase.getBase(creatureSelectBox.getSelected());
					edit.nature = new CreatureNature(positiveNature.getSelected(), negativeNature.getSelected());
					
					edit.vitaeAttunement = Byte.parseByte(vitaeEdit.getText()); 
					edit.focusAttunement = Byte.parseByte(focusEdit.getText());   
					edit.magicAtkAttunement = Byte.parseByte(magicAtkEdit.getText());
					edit.magicDefAttunement = Byte.parseByte(magicDefEdit.getText());
					edit.physAtkAttunement = Byte.parseByte(physAtkEdit.getText());
					edit.physDefAttunement = Byte.parseByte(physDefEdit.getText());
					edit.penAttunement = Byte.parseByte(penetrationEdit.getText());
					edit.resAttunement = Byte.parseByte(resistanceEdit.getText());
					edit.speedAttunement = Byte.parseByte(speedEdit.getText());
					
					edit.baseHealth = (int) baseHealthSlider.getValue();
					edit.baseMana = (int) baseManaSlider.getValue();
					
					edit.currentLevel = (int) levelSlider.getValue();
					edit.moves = movesActiveList.getItems().toArray(String.class);
					edit.personalName = creatureNameEdit.getText().equals("") ? edit.base.name + " (LVL " + edit.currentLevel + ")" : creatureNameEdit.getText();
					
					edit.recalculateAllStats(true);
					
					editedCreature = edit;
					setVisible(false);
					isEditComplete = true;
				}
				catch (NumberFormatException nfe) {
					System.out.println("Input stat values not parseable as bytes");
					nfe.printStackTrace();
					isEditComplete = false;
				}
			}
		});
		
		TextButton addRandomOne = new TextButton("Add Random LVL 1", UI.DEFAULT_SKIN);
		addRandomOne.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String[] ids = CreatureDatabase.getAvailableCreatureIDs().toArray(new String[]{});
				editedCreature = CreatureFactory.generateRandomLevelOne(ids[GameRandom.nextInt(ids.length)]);
				show(editedCreature);
			}
		});
		
		TextButton addRandomHundred = new TextButton("Add Random LVL 100", UI.DEFAULT_SKIN);
		addRandomHundred.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String[] ids = CreatureDatabase.getAvailableCreatureIDs().toArray(new String[]{});
				editedCreature = CreatureFactory.generateRandomLevelOne(ids[GameRandom.nextInt(ids.length)]);
				editedCreature.currentLevel = 100;
				levelSlider.setValue(100);
				levelDisplay.setText(100);
				show(editedCreature);
			}
		});
		
		add(editCancel, acceptButton, addRandomOne, addRandomHundred);
		
		pack();
		
		//Create move select window
		moveSelectWindow = new Window("Move Selection", UI.DEFAULT_SKIN);
		moveSelectWindow.setVisible(false);
		
		Label loadedMoves = new Label("Loaded Moves", UI.DEFAULT_SKIN);
		movesAvailableList = new List<>(UI.DEFAULT_SKIN);
		movesAvailableList.setItems(MoveDatabase.getLoadedMoveNames().toArray(new String[]{}));
		movesAvailableList.addListener(new ClickListener() {
			
			long prevClickTime = TimeUtils.millis();
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				long current = TimeUtils.millis();
				if(current - prevClickTime < 300 && movesAvailableList.getSelected() != null) {
					String toAdd = movesAvailableList.getSelected();
					if(!movesActiveList.getItems().contains(toAdd, true)) {
						Array<String> items = movesActiveList.getItems();
						items.add(toAdd);
						movesActiveList.setItems(items);
						moveSelectWindow.setVisible(false);
					}
				}
				prevClickTime = current;
			}
		});
		ScrollPane movesSelect = new ScrollPane(movesAvailableList, UI.DEFAULT_SKIN);
		movesSelect.setScrollbarsVisible(true);
		movesSelect.setFadeScrollBars(false);
		
		moveSelectWindow.add(loadedMoves);
		moveSelectWindow.row();
		moveSelectWindow.add(movesSelect).prefHeight(200).prefWidth(400);
		moveSelectWindow.row();
		
		TextButton movesCancel = new TextButton("Cancel", UI.DEFAULT_SKIN);
		movesCancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				moveSelectWindow.setVisible(false);
			}
		});
		TextButton movesAccept = new TextButton("Accept", UI.DEFAULT_SKIN);
		movesAccept.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String toAdd = movesAvailableList.getSelected();
				if(!movesActiveList.getItems().contains(toAdd, true)) {
					Array<String> items = movesActiveList.getItems();
					items.add(toAdd);
					movesActiveList.setItems(items);
					moveSelectWindow.setVisible(false);
				}
			}
		});
		Table movesButtons = new Table(UI.DEFAULT_SKIN);
		movesButtons.add(movesCancel, movesAccept);
		
		moveSelectWindow.add(movesButtons);
		
		moveSelectWindow.pack();
		ui.addActor(moveSelectWindow);
		
		moveSelectWindow.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Align.center);
		moveSelectWindow.setPosition(Math.round(moveSelectWindow.getX()), Math.round(moveSelectWindow.getY()));
	
		setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Align.center);
		setPosition(Math.round(getX()), Math.round(getY()));
		
		//Quick fix to update the default baseMana and baseHealth slider ranges
		creatureSelectBox.setSelectedIndex(1);
		creatureSelectBox.setSelectedIndex(0);
	}
	
	public void show(Creature c) {
		if(c != null) {
			CreatureBase b = c.base;
			
			creatureSelectBox.setSelected(b.name);
			creatureNameEdit.setText(c.personalName);
			
			levelSlider.setValue(c.currentLevel);
			baseHealthSlider.setRange(b.minBaseHealth, b.maxBaseHealth);
			baseHealthSlider.setValue(c.baseHealth);
			
			baseManaSlider.setRange(b.minBaseMana, b.maxBaseMana);
			baseManaSlider.setValue(c.baseMana);
			
			positiveNature.setSelected(c.nature.increased);
			negativeNature.setSelected(c.nature.decreased);
			
			vitaeEdit.setText(String.valueOf(c.vitaeAttunement));
			focusEdit.setText(String.valueOf(c.focusAttunement));
			magicAtkEdit.setText(String.valueOf(c.magicAtkAttunement));
			magicDefEdit.setText(String.valueOf(c.magicDefAttunement));
			physAtkEdit.setText(String.valueOf(c.physAtkAttunement));
			physDefEdit.setText(String.valueOf(c.physDefAttunement));
			penetrationEdit.setText(String.valueOf(c.penAttunement));
			resistanceEdit.setText(String.valueOf(c.resAttunement));
			speedEdit.setText(String.valueOf(c.speedAttunement));
	
			movesActiveList.setItems(c.moves);
		}
		else {
			resetCreatureStatsDisplay();
		}
		
		setVisible(true);
		isEditComplete = false;
	}
	
	public void addAcceptListener(ClickListener listener) {
		acceptButton.addListener(listener);
	}
	
	public Creature getEditedCreature() {
		return editedCreature;
	}
	
	public boolean isEditComplete() {
		return isEditComplete;
	}
	
	public void reloadMovesList() {
		movesAvailableList.setItems(MoveDatabase.getLoadedMoveNames().toArray(new String[]{}));
	}
	
	private void resetCreatureStatsDisplay() {
		creatureSelectBox.setSelectedIndex(0);
		creatureNameEdit.setText("");
		
		levelSlider.setValue(1);
		
		vitaeEdit.setText("16");
		focusEdit.setText("16");
		magicAtkEdit.setText("16");
		physAtkEdit.setText("16");
		physDefEdit.setText("16");
		resistanceEdit.setText("16");
		speedEdit.setText("16");
		
		movesActiveList.setItems();
	}
	
}
