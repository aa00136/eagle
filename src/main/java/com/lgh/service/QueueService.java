package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.MessageDao;
import com.lgh.model.db.Message;
import com.lgh.model.db.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息队列服务
 *
 * @author ligh
 * @create 2017-04-16 20:44
 **/
public class QueueService {
    private static ConcurrentHashMap<String, Map<String, LinkedBlockingQueue<Message>>> queueCache = new ConcurrentHashMap<String, Map<String, LinkedBlockingQueue<Message>>>(100);
    private static ConcurrentHashMap<String, Integer> cacheInfo = new ConcurrentHashMap<String, Integer>(100);
    private static MessageDao messageDao = new MessageDao();
    private static int queueLength = 1000;
    private static int waterMark = 500;

    public synchronized static List<Message> readMessage(String topicName, String clientName, int limit) throws ServiceException {
        Subscriber subscriber = SubscriberService.getSubscriber(clientName, topicName);
        if (subscriber == null) {
            throw new ServiceException(-1, "subscribe mapping not found");
        }
        Map<String, LinkedBlockingQueue<Message>> queueMap = queueCache.get(topicName);
        if (queueMap == null) {
            initQueue(subscriber);
            queueMap = queueCache.get(topicName);
        }
        LinkedBlockingQueue<Message> queueBuffer = queueMap.get(clientName);
        if (queueBuffer.size() < waterMark) {
            Integer loadCacheId = cacheInfo.get(getQueueKey(subscriber.getTopicName(), subscriber.getName()));
            if (loadCacheId != null) {
                loadDataFromDB(queueBuffer, subscriber.getTopicName(), subscriber.getName(), loadCacheId);
            } else {
                loadDataFromDB(queueBuffer, subscriber.getTopicName(), subscriber.getName(), subscriber.getMinConsumeMsgId());
            }
        }
        List<Message> messageList = new ArrayList<Message>();
        queueBuffer.drainTo(messageList, limit);
        if (messageList.size() > 0) {
            SubscriberService.updateSubscriber(clientName, topicName, messageList.get(messageList.size() - 1).getId(), null);
        }

        return messageList;
    }

    public static void writeMessage(Message message) throws ServiceException {
        messageDao.addMessage(message.getTopicName(), message.getContent());
    }

    public static LinkedBlockingQueue<Message> loadDataFromDB(LinkedBlockingQueue<Message> queueBuffer, String topicName, String clientName, int beginId) throws ServiceException {
        int count = queueLength - queueBuffer.size();
        List<Message> messageList = messageDao.listMessageById(topicName, beginId, count);
        queueBuffer.addAll(messageList);
        if (!messageList.isEmpty()) {
            cacheInfo.put(getQueueKey(topicName, clientName), messageList.get(messageList.size() - 1).getId());
        }
        return queueBuffer;
    }

    public static void initQueue(Subscriber subscriber) throws ServiceException {
        if (queueCache.get(subscriber.getTopicName()) == null) {
            Map<String, LinkedBlockingQueue<Message>> queueMap = new HashMap<String, LinkedBlockingQueue<Message>>();
            LinkedBlockingQueue<Message> queueBuffer = new LinkedBlockingQueue<Message>(queueLength);
            queueBuffer = loadDataFromDB(queueBuffer, subscriber.getTopicName(), subscriber.getName(), subscriber.getMinConsumeMsgId());
            queueMap.put(subscriber.getName(), queueBuffer);
            queueCache.put(subscriber.getTopicName(), queueMap);
        }
    }

    private static String getQueueKey(String topicName, String clientName) {
        return topicName + "_" + clientName;
    }
}