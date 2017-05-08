package com.lgh.model.command;


import com.lgh.constant.CommandCode;

public class PingCommandResp extends Command {

    public PingCommandResp(int requestId) {
        this.requestId=requestId;
		this.commandCode= CommandCode.PING_RSP;
		this.responseCode=1;
	}
}
