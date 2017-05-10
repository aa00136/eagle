package com.lgh.server;

/**
 * Created by ligh on 2017/5/10.
 */
public class ServerContextData {
    private int requestId;
    private String clientName;
    private String topicName;

    public ServerContextData(int requestId, String clientName, String topicName) {
        this.requestId = requestId;
        this.clientName = clientName;
        this.topicName = topicName;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
