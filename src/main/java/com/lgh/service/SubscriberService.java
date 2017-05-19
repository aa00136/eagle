package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.SubscriberDao;
import com.lgh.model.db.Subscriber;
import com.lgh.model.db.Topic;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订阅关系服务
 *
 * @author ligh
 * @create 2017-04-16 20:44
 **/
public class SubscriberService {
    public static ConcurrentHashMap<String, Subscriber> subscriberCache = new ConcurrentHashMap<String, Subscriber>(100);
    private static SubscriberDao subscriberDao = new SubscriberDao();

    public static void addSubscriber(String topicName, String clientName) throws ServiceException {
        Topic topic = TopicService.getTopicByName(topicName);
        if (topic != null) {
            Subscriber subscriber = getSubscriber(clientName, topicName);
            if (subscriber == null) {
                Integer maxId = TopicService.getQueueMaxMsgId(topicName);
                subscriber = new Subscriber();
                subscriber.setName(clientName);
                subscriber.setTopicId(topic.getId());
                subscriber.setMaxSendMsgId(maxId);
                subscriber.setMinConsumeMsgId(maxId);
                subscriber.setStatus(1);
                subscriber.setCreateTime(new Date());
                subscriberDao.addSubscriber(subscriber);
            }
            QueueService.initQueueCache(topic.getName(), subscriber.getName(), subscriber.getMinConsumeMsgId());
        } else {
            throw new ServiceException(-1, "topic is not exist");
        }
    }

    public static Subscriber getSubscriber(String clientName, String topicName) throws ServiceException {
        Subscriber subscriber = subscriberCache.get(clientName);
        if (subscriber == null) {
            subscriber = subscriberDao.getByClientNameAndTopicName(clientName, topicName);
            if (subscriber != null) {
                subscriberCache.put(subscriber.getName(), subscriber);
            }
        }
        return subscriber;
    }

    public static void updateSubscriber(String clientName, String topicName, Integer maxSendMsgId, Integer minConsumeMsgId) throws ServiceException {
        subscriberCache.remove(clientName);
        if (maxSendMsgId != null) {
            subscriberDao.updateMaxSendMsgId(clientName, topicName, maxSendMsgId);
        }
        if (minConsumeMsgId != null) {
            subscriberDao.updateMinConsumeMsgId(clientName, topicName, minConsumeMsgId);
        }
    }

    public static void deleteSubscriber(String topicName, String clientName) throws ServiceException {
        subscriberCache.remove(clientName);
        Topic topic = TopicService.getTopicByName(topicName);
        subscriberDao.deleteSubscriber(clientName, topic.getId());
    }

    public synchronized static void updateConsumeState(String topicName, String clientName, int msgId) throws ServiceException {
        Subscriber subscriber = getSubscriber(clientName, topicName);
        if (subscriber != null && subscriber.getMinConsumeMsgId() < msgId) {
            updateSubscriber(clientName, topicName, null, msgId);
        }
    }
}
