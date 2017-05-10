package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.SubscriberDao;
import com.lgh.dao.TopicDao;
import com.lgh.model.command.Command;
import com.lgh.model.db.Subscriber;
import com.lgh.model.db.Topic;
import com.lgh.util.GsonSerializeUtil;
import org.apache.commons.lang3.StringUtils;

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
        String topicName = (String) subscribeMap.get("topic_name");
        String clientName = (String) subscribeMap.get("client_name");
        if (StringUtils.isBlank(topicName) || StringUtils.isBlank(clientName)) {
            return;
        }

        Topic topic = topicDao.getTopicByName(topicName);
        if (topic != null) {
            Subscriber subscriber = getSubscriber(clientName, topicName);
            if (subscriber == null) {
                subscriber = new Subscriber();
                subscriber.setName(clientName);
                subscriber.setTopicName(topicName);
                subscriber.setMaxSendMsgId(0);
                subscriber.setMinConsumeMsgId(0);
                subscriber.setStatus(1);
                subscriber.setCreateTime(new Date());
                subscriberDao.addSubscriber(subscriber);
            }
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

    public static void deleteSubscriber(Command unsubscribeCommand) throws ServiceException {
        Map<String, Object> subscribeMap = GsonSerializeUtil.fromJson(unsubscribeCommand.getBody());
        String topicName = (String) subscribeMap.get("topic_name");
        String clientName = (String) subscribeMap.get("client_name");
        deleteSubscriber(clientName, topicName);
    }

    public static void deleteSubscriber(String clientName, String topicName) throws ServiceException {
        if (StringUtils.isBlank(clientName) || StringUtils.isBlank(topicName)) {
            throw new IllegalArgumentException("client_name or topic_name is blank");
        }
        subscriberDao.deleteSubscriber(clientName, topicName);
    }
}
