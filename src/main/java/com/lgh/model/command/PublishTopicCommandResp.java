package com.lgh.model.command;

import com.lgh.constant.CommandCode;

/**
 * Created by ligh on 2017/5/6.
 */
public class PublishTopicCommandResp extends Command {
    public PublishTopicCommandResp(int requestId, byte responseCode, String message) {
        this.requestId = requestId;
        this.commandCode = CommandCode.PUBLISH_TOPIC_RSP;
        this.responseCode = responseCode;
        this.body = message;
    }
}
