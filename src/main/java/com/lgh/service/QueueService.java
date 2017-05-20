package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.MessageDao;
import com.lgh.model.ConnectionConsumeState;
import com.lgh.model.db.Message;
import com.lgh.model.db.Subscriber;
import com.lgh.util.Log;

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
    private static ConcurrentHashMap<String, Map<Integer, ConnectionConsumeState>> consumeStateCache = new ConcurrentHashMap<String, Map<Integer, ConnectionConsumeState>>(100);
    private static ConcurrentHashMap<String, Integer> cacheInfo = new ConcurrentHashMap<String, Integer>(100);
    private static MessageDao messageDao = new MessageDao();
    private static int QUEUE_LENGTH = 1000;
    private static int WATER_MARK = 500;

    public synchronized static List<Message> readMessage(String topicName, String clientName, int limit) throws ServiceException {
        List<Message> messageList = new ArrayList<Message>();
        Subscriber subscriber = SubscriberService.getSubscriber(clientName, topicName);
        if (subscriber == null) {
            throw new ServiceException(-1, "subscribe mapping not found");
        }
        messageList = ConsumeInfoService.getTimeOutMessage(topicName, clientName);
        if (!messageList.isEmpty()) {
            return messageList;
        }

        Map<String, LinkedBlockingQueue<Message>> queueMap = queueCache.get(topicName);
        if (queueMap == null) {
            initQueueCache(topicName, clientName, subscriber.getMinConsumeMsgId());
            queueMap = queueCache.get(topicName);
        }
        LinkedBlockingQueue<Message> queueBuffer = queueMap.get(clientName);
        if (queueBuffer.size() < WATER_MARK) {
            Integer loadCacheId = cacheInfo.get(getQueueKey(topicName, clientName));
            if (loadCacheId != null) {
                loadDataFromDB(queueBuffer, topicName, clientName, loadCacheId);
            } else {
                loadDataFromDB(queueBuffer, topicName, clientName, subscriber.getMinConsumeMsgId());
            }
        }
        queueBuffer.drainTo(messageList, limit);
        if (messageList.size() > 0) {
            SubscriberService.updateSubscriber(clientName, topicName, messageList.get(messageList.size() - 1).getId(), null);
            ConsumeInfoService.setMessageConsumeState(topicName, clientName, messageList);
        }

        return messageList;
    }

    public static void writeMessage(Message message) throws ServiceException {
        messageDao.addMessage(message.getTopicName(), message.getContent());
    }

    public synchronized static LinkedBlockingQueue<Message> loadDataFromDB(LinkedBlockingQueue<Message> queueBuffer, String topicName, String clientName, int beginId) throws ServiceException {
        int count = QUEUE_LENGTH - queueBuffer.size();
        List<Message> messageList = messageDao.listMessageById(topicName, beginId, count);
        queueBuffer.addAll(messageList);
        if (!messageList.isEmpty()) {
            cacheInfo.put(getQueueKey(topicName, clientName), messageList.get(messageList.size() - 1).getId());

            Log.SERVER_QUEUE.info("topic_name=" + topicName + "`subscriber_name=" + clientName + "`load_msg_id=" + beginId);
        }
        return queueBuffer;
    }

    public static void initQueueCache(String topicName, String clientName, int beginId) throws ServiceException {
        Map<String, LinkedBlockingQueue<Message>> queueMap = queueCache.get(topicName);
        if (queueMap == null) {
            queueMap = new HashMap<String, LinkedBlockingQueue<Message>>();
            LinkedBlockingQueue<Message> queueBuffer = new LinkedBlockingQueue<Message>(QUEUE_LENGTH);
            queueBuffer = loadDataFromDB(queueBuffer, topicName, clientName, beginId);
            queueMap.put(clientName, queueBuffer);
            queueCache.put(topicName, queueMap);
        }
        if (queueMap.get(clientName) == null) {
            LinkedBlockingQueue<Message> queueBuffer = new LinkedBlockingQueue<Message>(QUEUE_LENGTH);
            queueBuffer = loadDataFromDB(queueBuffer, topicName, clientName, beginId);
            queueMap.put(clientName, queueBuffer);
            queueCache.put(topicName, queueMap);
        }
    }

    private static String getQueueKey(String topicName, String clientName) {
        return topicName + "_" + clientName;
    }
}