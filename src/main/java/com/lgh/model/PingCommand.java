package com.lgh.model;


import com.lgh.constant.CommandCode;
import com.lgh.util.IDGenerator;

public class PingCommand extends Command {

	public PingCommand() {
		requestId= IDGenerator.getRequestId();
		commandCode= CommandCode.PING_REQ;
	}

	
}
