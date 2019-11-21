package com.ccode.alchemonsters.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaClosure;

import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.BattleContext;
import com.ccode.alchemonsters.combat.BattleEvent;
import com.ccode.alchemonsters.combat.BattleTeam;
import com.ccode.alchemonsters.combat.Battleground;
import com.ccode.alchemonsters.combat.Catalyst;
import com.ccode.alchemonsters.combat.CombatState;
import com.ccode.alchemonsters.combat.CreatureTeam;
import com.ccode.alchemonsters.combat.GroundType;
import com.ccode.alchemonsters.combat.PassiveAbility;
import com.ccode.alchemonsters.combat.StatBuffs;
import com.ccode.alchemonsters.combat.TerrainType;
import com.ccode.alchemonsters.combat.WeatherType;
import com.ccode.alchemonsters.combat.ailments.StatusAilment;
import com.ccode.alchemonsters.combat.ailments.StatusAilmentEffect;
import com.ccode.alchemonsters.combat.moves.Move;
import com.ccode.alchemonsters.combat.moves.MoveAction;
import com.ccode.alchemonsters.combat.moves.MoveActionAilmentApplicator;
import com.ccode.alchemonsters.combat.moves.MoveActionAilmentRemoval;
import com.ccode.alchemonsters.combat.moves.MoveActionChance;
import com.ccode.alchemonsters.combat.moves.MoveActionChooseRandom;
import com.ccode.alchemonsters.combat.moves.MoveActionCombine;
import com.ccode.alchemonsters.combat.moves.MoveActionDamage;
import com.ccode.alchemonsters.combat.moves.MoveActionRepeat;
import com.ccode.alchemonsters.combat.moves.MoveActionScript;
import com.ccode.alchemonsters.combat.moves.MoveActionSetGround;
import com.ccode.alchemonsters.combat.moves.MoveActionSetTerrain;
import com.ccode.alchemonsters.combat.moves.MoveActionSetWeather;
import com.ccode.alchemonsters.combat.moves.MoveActionStatModifier;
import com.ccode.alchemonsters.combat.moves.MoveTarget;
import com.ccode.alchemonsters.combat.moves.MoveType;
import com.ccode.alchemonsters.combat.moves.TurnType;
import com.ccode.alchemonsters.creature.Creature;
import com.ccode.alchemonsters.creature.CreatureBase;
import com.ccode.alchemonsters.creature.CreatureNature;
import com.ccode.alchemonsters.creature.CreatureStats;
import com.ccode.alchemonsters.creature.ElementType;
import com.ccode.alchemonsters.creature.StatType;
import com.ccode.alchemonsters.engine.event.Message;
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
import com.ccode.alchemonsters.util.DynamicVariables;
import com.ccode.alchemonsters.util.Pair;
import com.ccode.alchemonsters.util.Triple;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;

public class KryoCreator {

	private static final int WRITE_BUFFER_SIZE = 8192;
	private static final int OBJECT_BUFFER_SIZE = 8192;
	
	private static void registerClasses(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		
		//Generic Classes
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);
		kryo.register(String.class);
		kryo.register(String[].class);
		kryo.register(UUID.class, new UUIDSerializer());
		
		//com.ccode.alchemonsters.combat
		kryo.register(BattleAction.class);
		kryo.register(BattleAction.BattleActionType.class);
		kryo.register(BattleContext.class);
		kryo.register(BattleEvent.class);
		kryo.register(Battleground.class);
		kryo.register(BattleTeam.class);
		kryo.register(Catalyst.class);
		kryo.register(CombatState.class);
		kryo.register(CreatureTeam.class);
		kryo.register(GroundType.class);
		kryo.register(PassiveAbility.class);
		kryo.register(StatBuffs.class);
		kryo.register(TerrainType.class);
		kryo.register(WeatherType.class);
		
		//com.ccode.alchemonsters.combat.ailments
		kryo.register(StatusAilment.class);
		kryo.register(StatusAilmentEffect.class);
		
		//com.ccode.alchemonsters.combat.moves
		kryo.register(Move.class);
		kryo.register(MoveAction.class);
		kryo.register(MoveAction[].class);
		kryo.register(MoveActionAilmentApplicator.class);
		kryo.register(MoveActionAilmentRemoval.class);
		kryo.register(MoveActionChance.class);
		kryo.register(MoveActionChooseRandom.class);
		kryo.register(MoveActionCombine.class);
		kryo.register(MoveActionDamage.class);
		kryo.register(MoveActionRepeat.class);
		kryo.register(MoveActionScript.class);
		kryo.register(MoveActionSetGround.class);
		kryo.register(MoveActionSetTerrain.class);
		kryo.register(MoveActionSetWeather.class);
		kryo.register(MoveActionStatModifier.class);
		kryo.register(MoveTarget.class);
		kryo.register(MoveType.class);
		kryo.register(TurnType.class);
		
		kryo.register(LuaClosure.class);
		kryo.register(Globals.class);
		
		//com.ccode.alchemonsters.creature
		kryo.register(Creature.class);
		kryo.register(Creature[].class);
		kryo.register(CreatureBase.class);
		kryo.register(CreatureNature.class);
		kryo.register(CreatureStats.class);
		kryo.register(ElementType.class);
		kryo.register(ElementType[].class);
		kryo.register(StatType.class);
		
		//com.ccode.alchemonsters.engine.event.message
		kryo.register(Message.class);
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
		
		//com.ccode.alchemonsters.net
		kryo.register(NetActionSelected.class);
		kryo.register(NetActionSubmitted.class);
		kryo.register(NetBattleContextUpdate.class);
		kryo.register(NetErrorMessage.class);
		kryo.register(NetFilterAllActions.class);
		kryo.register(NetFilterAvailableActions.class);
		kryo.register(NetJoinSuccess.class);
		kryo.register(NetJoinVersus.class);
		kryo.register(NetRefreshControl.class);
		kryo.register(NetResetAvailableActions.class);
		kryo.register(NetSetActions.class);
		
		//com.ccode.alchemonsters.util
		kryo.register(DynamicVariables.class);
		kryo.register(Pair.class);
		kryo.register(Triple.class);
		
	}
	
	public static Server createServer() {
		Server server = new Server(WRITE_BUFFER_SIZE, OBJECT_BUFFER_SIZE);
		registerClasses(server);
		return server;
	}
	
	public static Client createClient() {
		Client client = new Client(WRITE_BUFFER_SIZE, OBJECT_BUFFER_SIZE);
		registerClasses(client);
		return client;
	}
	
	public static class UUIDSerializer extends Serializer<UUID> {
		
		public UUIDSerializer() {
			setImmutable(true);
		}

		@Override
		public void write(Kryo kryo, Output output, UUID uuid) {
			output.writeLong(uuid.getMostSignificantBits());
			output.writeLong(uuid.getLeastSignificantBits());
		}

		@Override
		public UUID read(Kryo kryo, Input input, Class<UUID> type) {
			return new UUID(input.readLong(), input.readLong());
		}
		
	}
	
}
