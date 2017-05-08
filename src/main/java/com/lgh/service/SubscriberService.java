package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.SubscriberDao;
import com.lgh.dao.TopicDao;
import com.lgh.model.command.Command;
import com.lgh.model.db.Subscriber;
import com.lgh.model.db.Topic;
import com.lgh.util.GsonSerializeUtil;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ligh on 2017/4/16.
 */
public class SubscriberService {
    public static ConcurrentHashMap<String,ConcurrentLinkedQueue<Subscriber>> topicQueue = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Subscriber>>(100);
    private static TopicDao topicDao = new TopicDao();
    private static SubscriberDao subscriberDao = new SubscriberDao();

    public static void addSubscriber(Command subscribeCommand) throws ServiceException {
        Map<String, Object> subscribeMap = GsonSerializeUtil.fromJson(subscribeCommand.getBody());
        Topic topic = topicDao.getTopicByName((String) subscribeMap.get("topic_name"));
        if (topic != null) {
            Subscriber subscriber = new Subscriber();
            subscriber.setName((String) subscribeMap.get("client_name"));
            subscriber.setTopicName((String) subscribeMap.get("topic_name"));
            subscriber.setMaxSendMsgId(0);
            subscriber.setMinConsumeMsgId(0);
            subscriber.setStatus(1);
            subscriber.setCreateTime(new Date());
            subscriberDao.add(subscriber);
        } else {
            throw new ServiceException(-1, "topic not found");
        }
    }

    public static Subscriber getSubscriber(String clientName, String topicName) throws ServiceException {
        return subscriberDao.getByClientNameAndTopicName(clientName, topicName);
    }

    public static void updateSubscriber(String clientName, String topicName, Integer maxSendMsgId, Integer minConsumeMsgId) throws ServiceException {
        if (maxSendMsgId != null) {
            subscriberDao.updateMaxSendMsgId(clientName, topicName, maxSendMsgId);
        }
        if (minConsumeMsgId != null) {
            subscriberDao.updateMinConsumeMsgId(clientName, topicName, minConsumeMsgId);
        }
    }
}
