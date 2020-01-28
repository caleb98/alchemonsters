package com.ccode.alchemonsters.combat.moves;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;

public class MoveInstance {

	public final Move move;
	public final Creature source;
	public final Creature[] targets;
	public final BattleContext context;
	
	private float accuracyOverride = 0;
	private boolean isAccuracyOverriden = false;
	
	private int manaCostOverride = 0;
	private boolean isManaOverriden = false;
	
	private int critStageOverride = 0;
	private boolean isCritOverriden = false;
	
	private ElementType typeOverride = null;
	private boolean isTypeOverriden = false;
	
	private int priorityOverride = 0;
	private boolean isPriorityOverriden = false;
	
	private int delayOverride = 0;
	private boolean isDelayOverriden = false;
	
	public MoveInstance(Move m, Creature source, Creature[] targets, BattleContext context) {
		move = m;
		this.source = source;
		this.targets = targets;
		this.context = context;
	}
	
	public float getAccuracy() {
		return isAccuracyOverriden ? accuracyOverride : move.accuracy;
	}
	
	public int getManaCost() {
		return isManaOverriden ? manaCostOverride : move.manaCost;
	}
	
	public int getCritStages() {
		return isCritOverriden ? critStageOverride : move.critStage;
	}
	
	public ElementType getElementType() {
		return isTypeOverriden ? typeOverride : move.elementType;
	}
	
	public int getPriority() {
		return isPriorityOverriden ? priorityOverride : move.priority;
	}
	
	public int getDelay() {
		return isDelayOverriden ? delayOverride : move.delayAmount;
	}
	
	public void setAccuracyOverride(float accuracy) {
		accuracyOverride = accuracy;
	}
	
	public void setManaCostOverride(int manaCost) {
		manaCostOverride = manaCost;
	}
	
	public void setCritStageOverride(int critStage) {
		critStageOverride = critStage;
	}
	
	public void setTypeOverride(ElementType type) {
		typeOverride = type;
	}
	
	public void setPriorityOverride(int priority) {
		priorityOverride = priority;
	}
	
	public void setDelayOverride(int delay) {
		delayOverride = delay;
	}
	
}













