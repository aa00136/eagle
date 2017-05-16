package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.MessageDao;
import com.lgh.model.command.Command;
import com.lgh.model.db.Message;
import com.lgh.model.db.Subscriber;
import com.lgh.util.GsonSerializeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ligh on 2017/4/16.
 */
public class QueueService {
    public static ConcurrentHashMap<String,Map<String, ConcurrentLinkedQueue<Message>>> topicQueue = new ConcurrentHashMap<String, Map<String,ConcurrentLinkedQueue<Message>>>(100);
    private static MessageDao messageDao = new MessageDao();

    public synchronized static List<Message> readMessage(Command pullCommand) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(pullCommand.getBody());
        String topicName = (String) body.get("topic_name");
        String clientName = (String) body.get("client_name");
        Double messageCount = (Double) body.get("limit");
        if (StringUtils.isBlank(topicName) || StringUtils.isBlank(clientName) || messageCount.intValue() <= 0) {
            throw new ServiceException(-1, "client_name or topic_name or limit is blank");
        }

        Subscriber subscriber = SubscriberService.getSubscriber(clientName, topicName);
        if (subscriber == null) {
            throw new ServiceException(-1, "subscribe mapping not found");
        }
        List<Message> messageList = messageDao.listMessageByMaxMsgId(subscriber.getTopicName(), subscriber.getMinConsumeMsgId(), messageCount.intValue());
        if (messageList != null && messageList.size() > 0) {
            SubscriberService.updateSubscriber(clientName, topicName, messageList.get(messageList.size() - 1).getId(), null);
        }

        return messageList;
    }

    public static void writeMessage(Command publishCommand) throws ServiceException {
        Message message = GsonSerializeUtil.fromJson(publishCommand.getBody(), Message.class);
        if (StringUtils.isBlank(message.getTopicName())) {
            throw new ServiceException(-1, "topic_name is blank");
        }

        messageDao.addMessage(message.getTopicName(), message.getContent());
    }
}