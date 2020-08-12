package com.ccode.alchemonsters.engine.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class EventManager {

	private static final HashMap<String, Set<Subscriber>> subscribers = new HashMap<>();
	private static final LinkedList<Message> messageQueue = new LinkedList<>();
	
	public static void addSubscriber(String messageId, Subscriber sub) {
		if(subscribers.containsKey(messageId)) {
			subscribers.get(messageId).add(sub);
		}
		else {
			HashSet<Subscriber> subs = new HashSet<>();
			subs.add(sub);
			subscribers.put(messageId, subs);
		}
	}
	
	public static void removeSubscriber(String messageId, Subscriber sub) {
		if(subscribers.containsKey(messageId)) {
			Set<Subscriber> subs = subscribers.get(messageId);
			subs.remove(sub);
			if(subs.size() == 0) {
				subscribers.remove(messageId);
			}
		}
	}

	public static void sendMessage(Message message) {
		System.out.println("[EventManager] Message received: " + message.id);
		Set<Subscriber> subs = subscribers.get(message.id);
		if(subs != null) {
			for(Subscriber s : subs) {
				s.handleMessage(message);
			}
		}
	}
	
}
