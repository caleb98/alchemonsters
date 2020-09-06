package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.kyper.yarn.Dialogue;
import com.kyper.yarn.Dialogue.LineResult;
import com.kyper.yarn.Dialogue.OptionResult;

public class DialogueSystem extends IteratingSystem {

	private Entity playerEntity;
	
	private float closestDialogue = -1f;
	private Entity dialogueInteractEntity;
	
	private boolean isDialogueActive = false;
	private Dialogue activeDialogue = null;
	private float dialogueStepTime = 0f;
	public LineResult currentLine;
	public OptionResult currentOptions;
	
	public DialogueSystem(Entity playerEntity) {
		super(Family.all(DialogueComponent.class).get());
		this.playerEntity = playerEntity;
	}
	
	@Override
	public void update(float deltaTime) {
		if(!isDialogueActive) {
			
			closestDialogue = -1f;
			dialogueInteractEntity = null;
			
			//Calls proccessEntity() on all entities
			super.update(deltaTime);
			
			if(dialogueInteractEntity != null) {
				
				DialogueComponent dialogue = Mappers.dialogueComponent.get(dialogueInteractEntity);
				if(Gdx.input.isKeyPressed(Keys.E)) {
					startDialogue();
				}
				else {
					dialogue.showDialogueStartButton = true;
				}
				
			}
			
		}
		else {
			
			//Check to see if the player is still within range of the active dialogue entity
			DialogueComponent dialogue = Mappers.dialogueComponent.get(dialogueInteractEntity);
			Vector2 playerPos = Mappers.transformComponent.get(playerEntity).position;
			
			if(!dialogue.dialogueSensor.testPoint(playerPos)) {
				
				endDialogue();
				
			}
			else {
				
				if(dialogueStepTime > 0) {
					dialogueStepTime -= deltaTime;
				}
				else {
					
					//Process the dialogue
					if(activeDialogue.isNextCommand()) {
						//TODO: dialogue commands
					}
					
					else if(activeDialogue.isNextLine()) {
						currentLine = activeDialogue.getNextAsLine();
						int words = currentLine.getText().split("\s+").length;
						if(words > 5) {
							dialogueStepTime = 2f + 0.2f * (words - 5);
						}
						else {
							dialogueStepTime = 2f;
						}
					}
					
					else if(activeDialogue.isNextOptions()) {
						
					}
					
					else if(activeDialogue.isNextComplete()) {
						if(activeDialogue.getNextAsComplete().next_node == null) {
							endDialogue();
						}
					}
					
				}
				
			}
			
		}
	}
	
	@Override	
	protected void processEntity(Entity entity, float deltaTime) {
		DialogueComponent dialogue = Mappers.dialogueComponent.get(entity);
		Vector2 playerPos = Mappers.transformComponent.get(playerEntity).position;

		dialogue.showDialogueStartButton = false;
		
		//See if this entity is close enough to have a dialogue
		if(dialogue.dialogueSensor.testPoint(playerPos)) {
			Vector2 sensorPos = dialogue.dialogueSensor.getBody().getWorldCenter();
			float dist = sensorPos.dst(playerPos);
			if(dist < closestDialogue || closestDialogue == -1f) {
				closestDialogue = dist;
				dialogueInteractEntity = entity;
			}
		}
	}
	
	private void startDialogue() {
		DialogueComponent dialogue = Mappers.dialogueComponent.get(dialogueInteractEntity);
		
		//Start the dialogue
		isDialogueActive = true;
		dialogue.isDialogueActive = true;
		activeDialogue = dialogue.dialogue;
		activeDialogue.start();
		dialogueStepTime = 0f;
	}
	
	private void endDialogue() {
		DialogueComponent dialogue = Mappers.dialogueComponent.get(dialogueInteractEntity);
		
		//Stop the dialogue
		isDialogueActive = false;
		dialogue.isDialogueActive = false;
		activeDialogue.stop();
		activeDialogue = null;
		dialogueStepTime = 0f;
	}
	
}
