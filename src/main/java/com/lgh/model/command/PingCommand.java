package com.lgh.model.command;


import com.lgh.constant.CommandCode;
import com.lgh.model.command.Command;
import com.lgh.util.IDGenerator;

public class PingCommand extends Command {

	public PingCommand() {
		requestId= IDGenerator.getRequestId();
		commandCode= CommandCode.PING_REQ;
	}
}
