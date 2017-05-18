package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.MessageDao;
import com.lgh.model.ConnectionConsumeState;
import com.lgh.model.db.Message;
import com.lgh.model.db.Subscriber;

import java.util.*;
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
    private static int CONSUME_TIME_OUT = 30;

    public synchronized static List<Message> readMessage(String topicName, String clientName, int limit) throws ServiceException {
        List<Message> messageList = new ArrayList<Message>();
        Subscriber subscriber = SubscriberService.getSubscriber(clientName, topicName);
        if (subscriber == null) {
            throw new ServiceException(-1, "subscribe mapping not found");
        }

        Map<Integer, ConnectionConsumeState> consumeStateMap = consumeStateCache.get(getQueueKey(topicName, clientName));
        if (consumeStateMap != null) {
            long minTime = System.currentTimeMillis();
            ConnectionConsumeState minSendState = null;
            Set<Map.Entry<Integer, ConnectionConsumeState>> entrySet = consumeStateMap.entrySet();
            for (Map.Entry<Integer, ConnectionConsumeState> entry : entrySet) {
                ConnectionConsumeState state = entry.getValue();
                if (minTime > state.getSendTime()) {//获取未应答连接中最久的消息
                    minTime = state.getSendTime();
                    minSendState = state;
                }
            }
            //超时重传
            long nowTime = System.currentTimeMillis();
            long period = (nowTime - minTime) / 1000;
            if (period > CONSUME_TIME_OUT && !minSendState.getSendMessages().isEmpty()) {
                messageList = minSendState.getSendMessages();
                minSendState.setSendTime(nowTime);

                return messageList;
            }
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

            ConnectionConsumeState consumState = new ConnectionConsumeState();
            consumState.setLastMsgId(messageList.get(messageList.size() - 1).getId());
            consumState.setSendMessages(messageList);
            consumState.setSendTime(System.currentTimeMillis());
            Map<Integer, ConnectionConsumeState> currentStateMap = consumeStateCache.get(getQueueKey(topicName, clientName));
            if (currentStateMap == null) {
                currentStateMap = new HashMap<Integer, ConnectionConsumeState>();
            }
            currentStateMap.put(consumState.getLastMsgId(), consumState);
            consumeStateCache.put(getQueueKey(topicName, clientName), currentStateMap);
        }

        return messageList;
    }

    public static void writeMessage(Message message) throws ServiceException {
        messageDao.addMessage(message.getTopicName(), message.getContent());
    }

    public static LinkedBlockingQueue<Message> loadDataFromDB(LinkedBlockingQueue<Message> queueBuffer, String topicName, String clientName, int beginId) throws ServiceException {
        int count = QUEUE_LENGTH - queueBuffer.size();
        List<Message> messageList = messageDao.listMessageById(topicName, beginId, count);
        queueBuffer.addAll(messageList);
        if (!messageList.isEmpty()) {
            cacheInfo.put(getQueueKey(topicName, clientName), messageList.get(messageList.size() - 1).getId());
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

    public synchronized static void removeConsumeStateCache(String topicName, String clientName, int msgId) {
        Map<Integer, ConnectionConsumeState> currentStateMap = consumeStateCache.get(getQueueKey(topicName, clientName));
        if (currentStateMap != null) {
            currentStateMap.remove(msgId);
        }
    }

    private static String getQueueKey(String topicName, String clientName) {
        return topicName + "_" + clientName;
    }
}