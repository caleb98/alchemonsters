package com.ccode.alchemonsters.engine;

public abstract class Process {

	private ProcessState state;
	private Process child;
	
	//Lifecycle methods
	public void onInit() { state = ProcessState.RUNNING; }
	public void onUpdate(float delta) {}
	public void onSuccess() {}
	public void onAbort() {}
	public void onFail() {}
	
	//Functions for ending the process
	protected final void succeed() {
		state = ProcessState.SUCCEEDED;
	}
	
	protected final void fail() {
		state = ProcessState.FAILED;
	}
	
	//State management/checking
	public ProcessState getState() {
		return state;
	}
	
	public boolean isAlive() {
		return state == ProcessState.RUNNING || state == ProcessState.PAUSED;
	}
	
	public boolean isDead() {
		return state == ProcessState.SUCCEEDED || state == ProcessState.ABORTED || state == ProcessState.FAILED;
	}
	
	public boolean isPaused() {
		return state == ProcessState.PAUSED;
	}

	//Child methods
	public Process getChild() {
		return child;
	}
	
	public boolean hasChild() {
		return child == null;
	}
	
	public void attachChild(Process child) {
		this.child = child;
	}
	
	public enum ProcessState {
		//Uninitialized Processes
		UNINITIALIZED,
		
		//Living Processes
		RUNNING,
		PAUSED,
		
		//Dead Processes
		SUCCEEDED,
		ABORTED,
		FAILED,
	}
	
}
