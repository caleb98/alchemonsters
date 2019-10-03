package com.ccode.alchemonsters.engine.database;

import java.util.HashMap;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.ailments.StatusAilmentEffect;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.engine.event.Message;
import com.ccode.alchemonsters.engine.event.Subscriber;
import com.ccode.alchemonsters.engine.event.messages.MCombatAilmentApplied;
import com.ccode.alchemonsters.engine.event.messages.MCombatAilmentRemoved;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;

public class StatusAilmentDatabase {

	private static HashMap<String, StatusAilmentEffect> STATUS_AILMENTS;
	private static boolean isInitialized = false;
	
	public static void init() {
		STATUS_AILMENTS = new HashMap<String, StatusAilmentEffect>();
		
		//Initialize all ailments here
		registerAilments(
				new StatusAilmentEffect("Void Pull") {
					@Override
					public void handleMessage(Message m, BattleContext context, Creature source, Creature target, String cause) {
						if(m instanceof MCombatDamageDealt) {
							MCombatDamageDealt damage = (MCombatDamageDealt) m;
							if(!damage.isTriggered && damage.target == target) {
								source.currentHealth -= damage.amount / 4;
								publish(new MCombatDamageDealt(context, source, target, id, ElementType.VOID, damage.amount / 4, true, false, true));
							}
						}
					}

					@Override
					public void apply(Subscriber subscriber, BattleContext context, Creature source, Creature target, String cause) {
						subscriber.subscribe(MCombatDamageDealt.ID);
						publish(new MCombatAilmentApplied(context, source, target, cause, id));
					}

					@Override
					public void remove(Subscriber subscriber, BattleContext context, Creature source, Creature target, String cause) {
						subscriber.unsubscribe(MCombatDamageDealt.ID);
						publish(new MCombatAilmentRemoved(context, source, target, cause, id));
					}
				}
		);
		
		isInitialized = true;
	}
	
	public static void registerAilments(StatusAilmentEffect... ailments) {
		for(StatusAilmentEffect ailment : ailments) {
			if(STATUS_AILMENTS.containsKey(ailment.id)) {
				System.err.printf("[ERROR] Unable to register new status ailment with name \'%s\'. An ailment with this name already exists.\n", ailment.id);
				continue;
			}
			
			STATUS_AILMENTS.put(ailment.id, ailment);
			System.out.printf("Registered ailment \'%s\'.\n", ailment.id);
		}
	}
	
	public static StatusAilmentEffect getAilment(String name) {
		if(!isInitialized) {
			throw new IllegalStateException("StatusAilmentDictionary not intialized.");
		}
		
		if(!STATUS_AILMENTS.containsKey(name)) {
			System.err.printf("[Error] Unable to find status ailment of name \'%s\' in StatusAilmentDictionary.\n", name);
			return null;
		}
		else {
			return STATUS_AILMENTS.get(name);
		}
	}
	
}
