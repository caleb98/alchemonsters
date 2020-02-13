package com.ccode.alchemonsters.engine.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EventManager {

	private static final HashMap<String, Set<Subscriber>> subscribers = new HashMap<>();
	private static final HashMap<String, Set<Subscriber>> toAdd = new HashMap<>();
	private static final HashMap<String, Set<Subscriber>> toRemove = new HashMap<>();
	
	public static void addSubscriber(String messageId, Subscriber sub) {
		if(toAdd.containsKey(messageId)) {
			toAdd.get(messageId).add(sub);
		}
		else {
			HashSet<Subscriber> subs = new HashSet<>();
			subs.add(sub);
			toAdd.put(messageId, subs);
		}
	}
	
	public static void removeSubscriber(String messageId, Subscriber sub) {
		if(toRemove.containsKey(messageId)) {
			toRemove.get(messageId).add(sub);
		}
		else {
			HashSet<Subscriber> subs = new HashSet<>();
			subs.add(sub);
			toRemove.put(messageId, subs);
		}
	}

	public static void sendMessage(Message message) {
		//Add any unadded listeners for this message id
		if(toAdd.containsKey(message.id)) {
			
			Set<Subscriber> adding = toAdd.get(message.id);
			for(Subscriber sub : adding) {
				
				if(subscribers.containsKey(message.id)) {
					subscribers.get(message.id).add(sub);
				}
				else {
					HashSet<Subscriber> subs = new HashSet<>();
					subs.add(sub);
					subscribers.put(message.id, subs);
				}
				
			}
			adding.clear();
		}
		
		//Remove any unremoved listeners for this message id
		if(toRemove.containsKey(message.id)) {
			
			Set<Subscriber> removing = toRemove.get(message.id);
			for(Subscriber sub : removing) {
				
				if(subscribers.containsKey(message.id)) {
					Set<Subscriber> subs = subscribers.get(message.id);
					subs.remove(sub);
					if(subs.size() == 0) {
						subscribers.remove(message.id);
					}
				}
				
			}
			removing.clear();
		}
		
		System.out.println("[EventManager] Message received: " + message.id);
		Set<Subscriber> subs = subscribers.get(message.id);
		if(subs != null) {
			for(Subscriber s : subs) {
				s.handleMessage(message);
			}
			
		}
	}
	
}
