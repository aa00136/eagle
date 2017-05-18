package com.lgh.model;

import com.lgh.model.db.Message;

import java.util.List;

public class ConnectionConsumeState {
    private Integer lastMsgId;//最后推送的msgId
    private List<Message> sendMessages;//推送的消息
    private long sendTime; //发送时间

    public Integer getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(Integer lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public List<Message> getSendMessages() {
        return sendMessages;
    }

    public void setSendMessages(List<Message> sendMessages) {
        this.sendMessages = sendMessages;
    }
}
