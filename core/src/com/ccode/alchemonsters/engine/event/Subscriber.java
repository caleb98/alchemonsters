package com.ccode.alchemonsters.engine.event;

public interface Subscriber {

	default void subscribe(String messageId) {
		EventManager.addSubscriber(messageId, this);
	}
	
	default void unsubscribe(String messageId) {
		EventManager.removeSubscriber(messageId, this);
	}
	
	void handleMessage(Message currentMessage);

}
