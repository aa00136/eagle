package com.lgh.client;

public class ClientConfig {
    public final static int MAX_RECONNECT_COUNT = 10;
    private String host;
    private int port;
    private String topicName;
    private String clientName;

    public ClientConfig(String host, int port, String clientName) {
        this.host = host;
        this.port = port;
        this.clientName = clientName;
    }

    public static int getMaxReconnectCount() {
        return MAX_RECONNECT_COUNT;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
