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

/**
 * Created by ligh on 2017/4/16.
 */
public class SubscriberService {
    public static ConcurrentHashMap<String, Subscriber> subscriberCache = new ConcurrentHashMap<String, Subscriber>(100);
    private static TopicDao topicDao = new TopicDao();
    private static SubscriberDao subscriberDao = new SubscriberDao();

    public static void addSubscriber(Command subscribeCommand) throws ServiceException {
        Map<String, Object> subscribeMap = GsonSerializeUtil.fromJson(subscribeCommand.getBody());
        String topicName = (String) subscribeMap.get("topic_name");
        String clientName = (String) subscribeMap.get("client_name");
        if (StringUtils.isBlank(topicName) || StringUtils.isBlank(clientName)) {
            throw new ServiceException(-1, "client_name or topic_name is blank");
        }

        Topic topic = topicDao.getTopicByName(topicName);
        if (topic != null) {
            Subscriber subscriber = getSubscriber(clientName, topicName);
            if (subscriber == null) {
                Integer maxId = topicDao.getQueueMaxMsgId(topicName);
                subscriber = new Subscriber();
                subscriber.setName(clientName);
                subscriber.setTopicName(topicName);
                subscriber.setMaxSendMsgId(maxId);
                subscriber.setMinConsumeMsgId(maxId);
                subscriber.setStatus(1);
                subscriber.setCreateTime(new Date());
                subscriberDao.addSubscriber(subscriber);
            }
            QueueService.initQueue(subscriber);
        } else {
            throw new ServiceException(-1, "topic is not exist");
        }
    }

    public static Subscriber getSubscriber(String clientName, String topicName) throws ServiceException {
        Subscriber subscriber = subscriberCache.get(clientName);
        if (subscriber == null) {
            subscriber = subscriberDao.getByClientNameAndTopicName(clientName, topicName);
            subscriberCache.put(subscriber.getName(), subscriber);
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

    public static void deleteSubscriber(Command unsubscribeCommand) throws ServiceException {
        Map<String, Object> subscribeMap = GsonSerializeUtil.fromJson(unsubscribeCommand.getBody());
        String topicName = (String) subscribeMap.get("topic_name");
        String clientName = (String) subscribeMap.get("client_name");
        deleteSubscriber(clientName, topicName);
    }

    public static void deleteSubscriber(String clientName, String topicName) throws ServiceException {
        if (StringUtils.isBlank(clientName) || StringUtils.isBlank(topicName)) {
            throw new ServiceException(-1, "client_name or topic_name is blank");
        }
        subscriberCache.remove(clientName);
        subscriberDao.deleteSubscriber(clientName, topicName);
    }

    public synchronized static void updateConsumeState(Command pullAckCommand) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(pullAckCommand.getBody());
        String topicName = (String) body.get("topic_name");
        String clientName = (String) body.get("client_name");
        Double msg_id = (Double) body.get("msg_id");
        if (StringUtils.isBlank(topicName) || StringUtils.isBlank(clientName) || msg_id.intValue() <= 0) {
            throw new ServiceException(-1, "client_name or topic_name or msg_id is blank");
        }

        Subscriber subscriber = getSubscriber(clientName, topicName);
        if (subscriber.getMinConsumeMsgId() < msg_id.intValue()) {
            updateSubscriber(clientName, topicName, null, msg_id.intValue());
        }
    }
}
