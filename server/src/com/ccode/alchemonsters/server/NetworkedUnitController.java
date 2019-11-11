package com.ccode.alchemonsters.server;

import java.util.ArrayList;
import java.util.function.Predicate;

import com.ccode.alchemonsters.combat.BattleAction;
import com.ccode.alchemonsters.combat.GenericUnitController;
import com.ccode.alchemonsters.net.NetFilterAllActions;
import com.ccode.alchemonsters.net.NetFilterAvailableActions;
import com.ccode.alchemonsters.net.NetRefreshControl;
import com.ccode.alchemonsters.net.NetResetAvailableActions;
import com.ccode.alchemonsters.net.NetSetActions;
import com.esotericsoftware.kryonet.Connection;

public class NetworkedUnitController extends GenericUnitController {

	private Connection connection;
	private int activePos;
	
	public NetworkedUnitController(Connection connection, int activePos) {
		this.connection = connection;
		this.activePos = activePos;
	}
	
	@Override
	public void setAllActions(ArrayList<BattleAction> actions) {
		super.setAllActions(actions);
		connection.sendTCP(new NetSetActions(actions, activePos));
	}

	@Override
	public void filterAllActions(Predicate<BattleAction> filter) {
		super.filterAllActions(filter);
		connection.sendTCP(new NetFilterAllActions(filter, activePos));
	}

	@Override
	public void filterAvailableActions(Predicate<BattleAction> filter) {
		super.filterAvailableActions(filter);
		connection.sendTCP(new NetFilterAvailableActions(filter, activePos));
	}

	@Override
	public void resetAvailableActions() {
		super.resetAvailableActions();
		connection.sendTCP(new NetResetAvailableActions(activePos));
	}

	@Override
	public void refresh() {
		super.refresh();
		connection.sendTCP(new NetRefreshControl(activePos));
	}

}
