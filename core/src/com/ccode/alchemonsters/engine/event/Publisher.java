package com.ccode.alchemonsters.engine.event;

public interface Publisher {
	default void publish(Message message) {
		EventManager.addMessageToQueue(message);
	}
}
