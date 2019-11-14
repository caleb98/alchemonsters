package com.ccode.alchemonsters.net;

public class NetActionSelected {

	public int actionSelected;
	public int activePos;
	
	private NetActionSelected() {}
	
	public NetActionSelected(int action, int activePos) {
		actionSelected = action;
		this.activePos = activePos;
	}
	
}
