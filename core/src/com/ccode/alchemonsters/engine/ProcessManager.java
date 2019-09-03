package com.ccode.alchemonsters.engine;

import java.util.Iterator;
import java.util.LinkedList;

import com.ccode.alchemonsters.engine.Process.ProcessState;

public class ProcessManager {

	private LinkedList<Process> processList = new LinkedList<>();
	private LinkedList<Process> queuedProcesses = new LinkedList<>();
	
	public void updateProcesses(float delta) {
		//Add all queued processes
		while(queuedProcesses.peek() != null) {
			processList.add(queuedProcesses.poll());
		}
		
		Iterator<Process> iter = processList.iterator();
		while(iter.hasNext()) {
			Process p = iter.next();
			
			if(p.getState() == ProcessState.UNINITIALIZED) {
				p.onInit();
			}
			
			if(p.getState() == ProcessState.RUNNING) {
				p.onUpdate(delta);
			}
			
			if(p.isDead()) {
				switch(p.getState()) {
				
				case ABORTED:
					p.onAbort();
					break;
					
				case FAILED:
					p.onFail();
					break;
					
				case SUCCEEDED:
					p.onSuccess();
					if(p.hasChild()) {
						queuedProcesses.add(p.getChild());
					}
					break;
					
				default:
					//TODO: error handling
					break;
				
				}
				
				iter.remove();
			}
		}
	}
	
}
