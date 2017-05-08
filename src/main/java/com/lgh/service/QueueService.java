package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.MessageDao;
import com.lgh.dao.TopicDao;
import com.lgh.model.command.Command;
import com.lgh.model.db.Message;
import com.lgh.model.db.Subscriber;
import com.lgh.util.GsonSerializeUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ligh on 2017/4/16.
 */
public class QueueService {
    public static ConcurrentHashMap<String,Map<String, ConcurrentLinkedQueue<Message>>> topicQueue = new ConcurrentHashMap<String, Map<String,ConcurrentLinkedQueue<Message>>>(100);
    private static MessageDao messageDao = new MessageDao();
    private static TopicDao topicDao = new TopicDao();

    public static Message readMessage(Command pullCommand) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(pullCommand.getBody());
        String clientName = (String) body.get("client_name");
        Subscriber subscriber = SubscriberService.getSubscriber((String) body.get("client_name"));
        Message message = messageDao.getMessageByMaxMsgId(subscriber.getTopicName(), subscriber.getMaxSendMsgId());
        SubscriberService.updateSubscriber(clientName, message.getId(), null);

        return message;
    }

    public static void writeMessage(Command publishCommand) throws ServiceException {
        Message message = GsonSerializeUtil.fromJson(publishCommand.getBody(), Message.class);
        messageDao.addMessage(message.getTopicName(), message.getContent());
    }

    public static void createTopic(Command publishTopicCommand) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(publishTopicCommand.getBody());
        topicDao.createTopic((String) body.get("topic_name"));
    }
}