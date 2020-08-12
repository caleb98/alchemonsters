package com.ccode.alchemonsters.engine.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class EventManager implements Runnable {

	private static final Object lock = new Object();
	
	private static final HashMap<String, Set<Subscriber>> subscribers = new HashMap<>();
	private static final LinkedList<Message> messageQueue = new LinkedList<>();
	private static Thread managerThread;
	
	private static boolean isInitialized = false;
	
	private EventManager() {}
	
	public static void init() {
		if(!isInitialized) {
			managerThread = new Thread(new EventManager(), "EventManagerThread");
			managerThread.start();
			isInitialized = true;
		}
	}
	
	@Override
	public void run() {
		while(true) {
			synchronized(lock) {
				Message m;
				while((m = messageQueue.poll()) != null) {
					System.out.println("[EventManager] Message received: " + m.id);
					Set<Subscriber> subs = subscribers.get(m.id);
					if(subs != null) {
						for(Subscriber s : subs) {
							s.handleMessage(m);
						}
					}
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	public static void addSubscriber(String messageId, Subscriber sub) {
		synchronized(lock) {
			if(subscribers.containsKey(messageId)) {
				subscribers.get(messageId).add(sub);
			}
			else {
				HashSet<Subscriber> subs = new HashSet<>();
				subs.add(sub);
				subscribers.put(messageId, subs);
			}
		}
	}
	
	public static void removeSubscriber(String messageId, Subscriber sub) {
		synchronized(lock) {
			if(subscribers.containsKey(messageId)) {
				Set<Subscriber> subs = subscribers.get(messageId);
				subs.remove(sub);
				if(subs.size() == 0) {
					subscribers.remove(messageId);
				}
			}
		}
	}

	public static void sendMessage(Message message) {
		synchronized(lock) {
			messageQueue.add(message);
		}
	}
	
}
