package com.ccode.alchemonsters.event;

public interface Publisher {
	default void publish(Message message) {
		EventManager.addMessageToQueue(message);
	}
}
