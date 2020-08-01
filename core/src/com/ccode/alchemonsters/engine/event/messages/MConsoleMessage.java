package com.ccode.alchemonsters.engine.event.messages;

import com.ccode.alchemonsters.engine.event.Message;

public class MConsoleMessage extends Message {

	public static final String ID = "GENERIC_CONSOLE_MESSAGE";
	
	public final String message;
	
	public MConsoleMessage(String message) {
		super(ID);
		this.message = message;
	}

}
