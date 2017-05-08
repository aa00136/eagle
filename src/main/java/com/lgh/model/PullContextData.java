package com.lgh.model;

import com.lgh.client.CommandClient;

/**
 * Created by ligh on 2017/5/8.
 */
public class PullContextData {
    private CommandClient client;
    private String topicName;
    private Integer lastMsgId;

    public PullContextData(CommandClient client, String topicName, Integer lastMsgId) {
        this.client = client;
        this.topicName = topicName;
        this.lastMsgId = lastMsgId;
    }

    public CommandClient getClient() {
        return client;
    }

    public void setClient(CommandClient client) {
        this.client = client;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(Integer lastMsgId) {
        this.lastMsgId = lastMsgId;
    }
}
