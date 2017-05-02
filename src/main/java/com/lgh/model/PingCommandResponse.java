package com.lgh.model;


import com.lgh.constant.CommandCode;

public class PingCommandResponse extends Command {

	public PingCommandResponse(int requestId) {
		this.requestId=requestId;
		this.commandCode= CommandCode.PING_RSP;
		this.responseCode=1;
	}
}
