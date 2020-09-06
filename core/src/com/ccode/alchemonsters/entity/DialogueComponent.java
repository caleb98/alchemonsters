package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.kyper.yarn.Dialogue;
import com.kyper.yarn.Dialogue.LineResult;
import com.kyper.yarn.Dialogue.OptionResult;

public class DialogueComponent implements Component {

	public Dialogue dialogue;
	public Fixture dialogueSensor;
	public boolean showDialogueStartButton = false;
	
	public boolean isDialogueActive = false;
	public LineResult currentLine;
	public OptionResult currentOptions;
	
	public DialogueComponent(Dialogue dialogue, Fixture dialogueSensor) {
		this.dialogue = dialogue;
		this.dialogueSensor = dialogueSensor;
	}
	
}
