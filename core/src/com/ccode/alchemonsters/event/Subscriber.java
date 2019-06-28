package com.ccode.alchemonsters.event;

public interface Subscriber {

	void subscribe(String messageId);
	void unsubscribe(String messageId);
	void handleMessage(Message currentMessage);

}
