package com.ccode.alchemonsters.net;

import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.engine.event.messages.MCombatAilmentApplied;
import com.ccode.alchemonsters.engine.event.messages.MCombatAilmentRemoved;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatChargeStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatDamageDealt;
import com.ccode.alchemonsters.engine.event.messages.MCombatFinished;
import com.ccode.alchemonsters.engine.event.messages.MCombatGroundChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatHealingReceived;
import com.ccode.alchemonsters.engine.event.messages.MCombatStarted;
import com.ccode.alchemonsters.engine.event.messages.MCombatStatBuffApplied;
import com.ccode.alchemonsters.engine.event.messages.MCombatStateChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatTeamActiveChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatTerrainChanged;
import com.ccode.alchemonsters.engine.event.messages.MCombatWeatherChanged;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;

public class KryoCreator {

	private static void registerClasses(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		
		//Combat specific classes
		kryo.register(BattleContext.class);
		
		//Register message classes
		kryo.register(MCombatAilmentApplied.class);
		kryo.register(MCombatAilmentRemoved.class);
		kryo.register(MCombatChargeFinished.class);
		kryo.register(MCombatChargeStarted.class);
		kryo.register(MCombatDamageDealt.class);
		kryo.register(MCombatFinished.class);
		kryo.register(MCombatGroundChanged.class);
		kryo.register(MCombatHealingReceived.class);
		kryo.register(MCombatStarted.class);
		kryo.register(MCombatStatBuffApplied.class);
		kryo.register(MCombatStateChanged.class);
		kryo.register(MCombatTeamActiveChanged.class);
		kryo.register(MCombatTerrainChanged.class);
		kryo.register(MCombatWeatherChanged.class);
		
		//Network-specific classes
		kryo.register(NetActionSelected.class);
		kryo.register(NetErrorMessage.class);
		kryo.register(NetFilterAllActions.class);
		kryo.register(NetFilterAvailableActions.class);
		kryo.register(NetJoinVersus.class);
		kryo.register(NetRefreshControl.class);
		kryo.register(NetResetAvailableActions.class);
		kryo.register(NetSetActions.class);
		
	}
	
	public static Server createServer() {
		Server server = new Server();
		registerClasses(server);
		return server;
	}
	
	public static Client createClient() {
		Client client = new Client();
		registerClasses(client);
		return client;
	}
	
}
