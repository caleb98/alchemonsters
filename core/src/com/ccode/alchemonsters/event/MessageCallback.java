package com.ccode.alchemonsters.event;

@FunctionalInterface
public interface MessageCallback {
	public void callback(Message message);
}
