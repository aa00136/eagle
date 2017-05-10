package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.MessageDao;
import com.lgh.dao.TopicDao;
import com.lgh.model.command.Command;
import com.lgh.model.db.Message;
import com.lgh.model.db.Subscriber;
import com.lgh.model.db.Topic;
import com.lgh.util.GsonSerializeUtil;

import java.util.Date;
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
    private static TopicDao topicDao = new TopicDao();

    public synchronized static List<Message> readMessage(Command pullCommand) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(pullCommand.getBody());
        String topicName = (String) body.get("topic_name");
        String clientName = (String) body.get("client_name");
        Double messageCount = (Double) body.get("limit");
        Subscriber subscriber = SubscriberService.getSubscriber(clientName, topicName);
        List<Message> messageList = messageDao.listMessageByMaxMsgId(subscriber.getTopicName(), subscriber.getMaxSendMsgId(), messageCount.intValue());
        if (messageList != null && messageList.size() > 0) {
            SubscriberService.updateSubscriber(clientName, topicName, messageList.get(messageList.size() - 1).getId(), null);
        }

        return messageList;
    }

    public static void writeMessage(Command publishCommand) throws ServiceException {
        Message message = GsonSerializeUtil.fromJson(publishCommand.getBody(), Message.class);
        messageDao.addMessage(message.getTopicName(), message.getContent());
    }

    public static void createTopic(Command publishTopicCommand) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(publishTopicCommand.getBody());
        String topicName = (String) body.get("topic_name");
        Topic topic = new Topic();
        topic.setName(topicName);
        topic.setCreateTime(new Date());
        topicDao.addTopic(topic);
        topicDao.createQueue(topicName);
    }

    public static void updateConsumeState(Command pullAckCommand) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(pullAckCommand.getBody());
        String topicName = (String) body.get("topic_name");
        String clientName = (String) body.get("client_name");
        Double msg_id = (Double) body.get("msg_id");
        SubscriberService.updateSubscriber(clientName, topicName, null, msg_id.intValue());
    }
}