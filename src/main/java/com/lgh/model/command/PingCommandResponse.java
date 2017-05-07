package com.lgh.model.command;


import com.lgh.constant.CommandCode;
import com.lgh.model.command.Command;

public class PingCommandResponse extends Command {

	public PingCommandResponse(int requestId) {
		this.requestId=requestId;
		this.commandCode= CommandCode.PING_RSP;
		this.responseCode=1;
	}
}
