package com.ccode.alchemonsters.net;

import com.ccode.alchemonsters.combat.GenericUnitController;
import com.esotericsoftware.kryonet.Connection;

public class ClientUnitController extends GenericUnitController {

	private Connection connection;
	private int activePos;
	
	public ClientUnitController(Connection conn, int activePos) {
		connection = conn;
		this.activePos = activePos;
	}
	
	@Override
	public void setSelectedAction(int selectedIndex) {
		super.setSelectedAction(selectedIndex);
		connection.sendTCP(new NetActionSelected(selectedIndex, activePos));
	}
	
	@Override
	public void submitAction() {
		super.submitAction();
		connection.sendTCP(new NetActionSubmitted(activePos));
	}
	
}
