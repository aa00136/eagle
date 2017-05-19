package com.lgh.task;


import com.huisa.common.exception.ServiceException;
import com.lgh.model.db.Message;
import com.lgh.service.ConsumeInfoService;
import com.lgh.util.Log;

import java.util.List;

public class ConsumeInfoUpdateTask implements Runnable {
    private String topicName;
    private String clientName;
    private List<Message> msgList;

    public ConsumeInfoUpdateTask(String topicName, String clientName, List<Message> msgList) {
        this.topicName = topicName;
        this.clientName = clientName;
        this.msgList = msgList;
    }

    public void run() {
        try {
            ConsumeInfoService.saveOrUpdateConsumeInfo(topicName, clientName, msgList);
        } catch (ServiceException e) {
            Log.SERVER_ERROR.error(e.getMessage(), e);
        }
    }
}
