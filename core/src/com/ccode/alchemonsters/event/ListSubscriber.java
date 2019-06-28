package com.ccode.alchemonsters.event;

import java.util.LinkedList;

/**
 * An implementation of the Subscriber interface that handles messages
 * by storing them in a private message queue.
 */
public class ListSubscriber implements Subscriber {

	protected LinkedList<Message> messageQueue = new LinkedList<>();
	
	@Override
	public void subscribe(String messageId) {
		EventManager.addSubscriber(messageId, this);
	}

	@Override
	public void unsubscribe(String messageId) {
		EventManager.removeSubscriber(messageId, this);
	}

	@Override
	public void handleMessage(Message currentMessage) {
		messageQueue.add(currentMessage);
	}

}
