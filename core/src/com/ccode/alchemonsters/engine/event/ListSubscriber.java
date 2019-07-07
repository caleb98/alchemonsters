package com.ccode.alchemonsters.engine.event;

import java.util.LinkedList;

/**
 * An implementation of the Subscriber interface that handles messages
 * by storing them in a protected message queue.
 */
public class ListSubscriber implements Subscriber {

	protected LinkedList<Message> messageQueue = new LinkedList<>();

	@Override
	public void handleMessage(Message currentMessage) {
		messageQueue.add(currentMessage);
	}
	
}
