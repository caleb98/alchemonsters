package com.ccode.alchemonsters.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class DialogueSystem extends IteratingSystem {

	private Entity playerEntity;
	private float closestDialogue = -1f;
	private Entity activeDialogueEntity;
	
	public DialogueSystem(Entity playerEntity) {
		super(Family.all(DialogueComponent.class).get());
		this.playerEntity = playerEntity;
	}
	
	@Override
	public void update(float deltaTime) {
		closestDialogue = -1f;
		activeDialogueEntity = null;
		super.update(deltaTime);
		
		if(activeDialogueEntity != null) {
			DialogueComponent dialogue = Mappers.dialogueComponent.get(activeDialogueEntity);
			dialogue.showDialogue = true;
		}
	}
	
	@Override	
	protected void processEntity(Entity entity, float deltaTime) {
		DialogueComponent dialogue = Mappers.dialogueComponent.get(entity);
		Vector2 playerPos = Mappers.transformComponent.get(playerEntity).position;
		
		dialogue.showDialogue = false;
		
		if(dialogue.dialogueSensor.testPoint(playerPos)) {
			Vector2 sensorPos = dialogue.dialogueSensor.getBody().getWorldCenter();
			float dist = sensorPos.dst(playerPos);
			if(dist < closestDialogue || closestDialogue == -1f) {
				closestDialogue = dist;
				activeDialogueEntity = entity;
			}
		}
	}
	
}
