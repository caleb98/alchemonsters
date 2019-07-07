package com.ccode.alchemonsters;

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
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.ccode.alchemonsters.combat.moves.MoveDictionary;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.CreatureBase;
import com.ccode.alchemonsters.creature.CreatureDictionary;
import com.ccode.alchemonsters.creature.CreatureNature;
import com.ccode.alchemonsters.creature.CreatureStats;
import com.ccode.alchemonsters.creature.StatType;

public class CreatureEditWindow extends Window {
	
	private SelectBox<String> creatureSelectBox;
	private Slider baseHealthSlider;
	private Label baseHealthDisplay;
	private Slider baseManaSlider;
	private Label baseManaDisplay;
	
	private TextField vitaeEdit;
	private TextField focusEdit;
	private TextField magicPowerEdit;
	private TextField magicPenEdit;
	private TextField magicResistEdit;
	private TextField physPowerEdit;
	private TextField physPenEdit;
	private TextField physResistEdit;
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
		for(String id : CreatureDictionary.getAvailableCreatureIDs()) {
			items.add(id);
		}
		creatureSelectBox.setItems(items);
		creatureSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				CreatureBase b = CreatureDictionary.getBase(creatureSelectBox.getSelected());
				baseHealthSlider.setRange(b.minBaseHealth, b.maxBaseHealth);
				baseManaSlider.setRange(b.minBaseMana, b.maxBaseMana);
			}
		});
		
		add(creatureSelectText, creatureSelectBox);
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
		
		Label vitaeLabel = new Label("Vitae: ", UI.DEFAULT_SKIN);
		vitaeEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label focusLabel = new Label("Focus: ", UI.DEFAULT_SKIN);
		focusEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label magicATKLabel = new Label("Magic Power: ", UI.DEFAULT_SKIN);
		magicPowerEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label magicPenLabel = new Label("Magic Pen: ", UI.DEFAULT_SKIN);
		magicPenEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label magicDEFLabel = new Label("Magic Resist: ", UI.DEFAULT_SKIN);
		magicResistEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label physATKLabel = new Label("Phys Power: ", UI.DEFAULT_SKIN);
		physPowerEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label physPenLabel = new Label("Phys Pen: ", UI.DEFAULT_SKIN);
		physPenEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label physDEFLabel = new Label("Phys Resist: ", UI.DEFAULT_SKIN);
		physResistEdit = new TextField("16", UI.DEFAULT_SKIN);
		Label speedLabel = new Label("Speed: ", UI.DEFAULT_SKIN);
		speedEdit = new TextField("16", UI.DEFAULT_SKIN);
		
		add(vitaeLabel, vitaeEdit);
		add(focusLabel, focusEdit);
		row();
		add(magicATKLabel, magicPowerEdit);
		add(physATKLabel, physPowerEdit);
		row();
		add(magicPenLabel, magicPenEdit);
		add(physPenLabel, physPenEdit);
		row();
		add(magicDEFLabel, magicResistEdit);
		add(physDEFLabel, physResistEdit);
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

		add(positiveNatureLabel, positiveNature);
		row();
		add(negativeNatureLabel, negativeNature);
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
				}
			}
		});
		TextButton addButton = new TextButton("Add", UI.DEFAULT_SKIN);
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				moveSelectWindow.setVisible(true);
				moveSelectWindow.toFront();
			}
		});
		moveButtons.add(removeButton);
		moveButtons.row();
		moveButtons.add(addButton);
		
		add(movesLabel);
		row();
		add(movesPane).prefHeight(100).prefWidth(Value.percentWidth(1f));
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
					CreatureBase base = CreatureDictionary.getBase(creatureSelectBox.getSelected());
					CreatureStats stats = new CreatureStats(
							Byte.parseByte(vitaeEdit.getText()), 
							Byte.parseByte(focusEdit.getText()), 
							Byte.parseByte(magicPowerEdit.getText()), 
							Byte.parseByte(magicPenEdit.getText()),
							Byte.parseByte(magicResistEdit.getText()), 
							Byte.parseByte(physPowerEdit.getText()),
							Byte.parseByte(physPenEdit.getText()),
							Byte.parseByte(physResistEdit.getText()), 
							Byte.parseByte(speedEdit.getText())
					);
					CreatureNature nature = new CreatureNature(positiveNature.getSelected(), negativeNature.getSelected());
					Creature c = new Creature(base, nature, stats);
					
					c.baseHealth = (int) baseHealthSlider.getValue();
					c.baseMana = (int) baseManaSlider.getValue();
					c.moves = movesActiveList.getItems().toArray(String.class);
					
					editedCreature = c;
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
		add(editCancel, acceptButton);
		
		pack();
		
		//Create move select window
		moveSelectWindow = new Window("Move Selection", UI.DEFAULT_SKIN);
		moveSelectWindow.setVisible(false);
		
		Label loadedMoves = new Label("Loaded Moves", UI.DEFAULT_SKIN);
		movesAvailableList = new List<>(UI.DEFAULT_SKIN);
		movesAvailableList.setItems(MoveDictionary.getLoadedMoveNames().toArray(new String[]{}));
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
		
		//Quick fix to update the default baseMana and baseHealth slider ranges
		creatureSelectBox.setSelectedIndex(1);
		creatureSelectBox.setSelectedIndex(0);
	}
	
	public void show(Creature c) {
		if(c != null) {
			CreatureBase b = c.base;
			
			creatureSelectBox.setSelected(b.id);
			
			baseHealthSlider.setRange(b.minBaseHealth, b.maxBaseHealth);
			baseHealthSlider.setValue(c.baseHealth);
			
			baseManaSlider.setRange(b.minBaseMana, b.maxBaseMana);
			baseManaSlider.setValue(c.baseMana);
			
			positiveNature.setSelected(c.nature.increased);
			negativeNature.setSelected(c.nature.decreased);
			
			vitaeEdit.setText(String.valueOf(c.getBaseVitae()));
			focusEdit.setText(String.valueOf(c.getBaseFocus()));
			magicPowerEdit.setText(String.valueOf(c.getBaseMagicAtk()));
			magicResistEdit.setText(String.valueOf(c.getBaseMagicDef()));
			physPowerEdit.setText(String.valueOf(c.getBasePhysAtk()));
			physResistEdit.setText(String.valueOf(c.getBasePhysDef()));
			speedEdit.setText(String.valueOf(c.getBaseSpeed()));
	
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
	
	private void resetCreatureStatsDisplay() {
		creatureSelectBox.setSelectedIndex(0);
		
		vitaeEdit.setText("16");
		focusEdit.setText("16");
		magicPowerEdit.setText("16");
		magicResistEdit.setText("16");
		physPowerEdit.setText("16");
		physResistEdit.setText("16");
		speedEdit.setText("16");
		
		movesActiveList.setItems();
	}
	
}