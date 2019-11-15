package com.ccode.alchemonsters.net;

public class NetErrorMessage {
	
	public static final int ERR_LOBBY_FULL = 0;
	public static final int ERR_JOIN_ERROR = 1;
	
	public String message;
	public int errno;
	
	private NetErrorMessage() {}
	
	public NetErrorMessage(String message, int errno) {
		this.message = message;
		this.errno = errno;
	}
	
}
