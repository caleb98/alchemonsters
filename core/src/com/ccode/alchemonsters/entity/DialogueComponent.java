package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.kyper.yarn.Dialogue;

public class DialogueComponent implements Component {

	public Dialogue dialogue;
	public Fixture dialogueSensor;
	public boolean showDialogue = false;
	
	public DialogueComponent(Dialogue dialogue, Fixture dialogueSensor) {
		this.dialogue = dialogue;
		this.dialogueSensor = dialogueSensor;
	}
	
}
