package com.ccode.alchemonsters.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.ccode.alchemonsters.util.Pair;

public class EventMessagingSystem {
	
	private static HashMap<Integer, ArrayList<MessageCallback>> callbacks;
	private static LinkedList<Pair<Integer, MessageCallback>> remove;
	private static LinkedList<Message> messageQueue;

	public static void init() {
		callbacks = new HashMap<>();
		remove = new LinkedList<>();
		messageQueue = new LinkedList<>();
	}
	
	public static void dispose() {
		
	}
	
	public static void update(float delta) {
		
		int size = messageQueue.size();
		Message next;
		while(size-- > 0) {
			next = messageQueue.pop();
			if(callbacks.containsKey(next.id)) {
				for(MessageCallback c : callbacks.get(next.id)) {
					c.callback(next);
				}
			}
		}
		
		Pair<Integer, MessageCallback> toRemove;
		while((toRemove = remove.poll()) != null) {
			ArrayList<MessageCallback> callbackList = callbacks.get(toRemove.a);
			if(callbackList != null) {
				 callbackList.remove(toRemove.b);
				 if(callbackList.size() == 0) {
					 callbacks.remove(toRemove.a);
				 }
			}
		}
	}
	
	public static void publishMessage(Message message) {
		messageQueue.add(message);
	}
	
	public static void subscribe(int messageId, MessageCallback callback) {
		if(callbacks.get(messageId) == null) {
			callbacks.put(messageId, new ArrayList<MessageCallback>());
		}
		
		callbacks.get(messageId).add(callback);
	}
	
	public static void unsubscribe(int messageId, MessageCallback callback) {
		remove.add(new Pair<>(messageId, callback));
	}
	
}
