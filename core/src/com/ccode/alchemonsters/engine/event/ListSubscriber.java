package com.ccode.alchemonsters.engine.event;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An implementation of the Subscriber interface that handles messages
 * by storing them in a protected message queue.
 */
public class ListSubscriber implements Subscriber {

	public ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();

	@Override
	public void handleMessage(Message currentMessage) {
		messageQueue.add(currentMessage);
	}
	
}
