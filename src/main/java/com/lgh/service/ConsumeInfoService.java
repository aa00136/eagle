package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.ConsumeInfoDao;
import com.lgh.model.ConnectionConsumeState;
import com.lgh.model.db.ConsumeInfo;
import com.lgh.model.db.Message;
import com.lgh.model.db.Subscriber;
import com.lgh.model.db.Topic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息消费记录服务
 *
 * @author ligh
 * @create 2017-05-19 14:02
 **/
public class ConsumeInfoService {
    private static ConcurrentHashMap<String, Map<Integer, ConnectionConsumeState>> consumeStateCache = new ConcurrentHashMap<String, Map<Integer, ConnectionConsumeState>>(100);
    private static ConsumeInfoDao consumeInfoDao = new ConsumeInfoDao();
    private static int CONSUME_TIME_OUT = 30;

    public static List<Message> getTimeOutMessage(String topicName, String clientName) {
        List<Message> messageList = new ArrayList<Message>();
        Map<Integer, ConnectionConsumeState> consumeStateMap = consumeStateCache.get(getCacheKey(topicName, clientName));
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
            }
        }

        return messageList;
    }

    public static void setMessgeConsumeState(String topicName, String clientName, List<Message> messageList) throws ServiceException {
        ConnectionConsumeState consumState = new ConnectionConsumeState();
        consumState.setLastMsgId(messageList.get(messageList.size() - 1).getId());
        consumState.setSendMessages(messageList);
        consumState.setSendTime(System.currentTimeMillis());
        Map<Integer, ConnectionConsumeState> currentStateMap = consumeStateCache.get(getCacheKey(topicName, clientName));
        if (currentStateMap == null) {
            currentStateMap = new HashMap<Integer, ConnectionConsumeState>();
        }
        currentStateMap.put(consumState.getLastMsgId(), consumState);
        consumeStateCache.put(getCacheKey(topicName, clientName), currentStateMap);
    }

    public static ConnectionConsumeState removeMessageConsumeState(String topicName, String clientName, int msgId) {
        Map<Integer, ConnectionConsumeState> currentStateMap = consumeStateCache.get(getCacheKey(topicName, clientName));
        if (currentStateMap != null) {
            ConnectionConsumeState consumeState = currentStateMap.remove(msgId);
            return consumeState;
        }
        return null;
    }

    public synchronized static void saveOrUpdateConsumeInfo(String topicName, String clientName, List<Message> messageList) throws ServiceException {
        Subscriber subscriber = SubscriberService.getSubscriber(clientName, topicName);
        Topic topic = TopicService.getTopicByName(topicName);
        for (Message message : messageList) {
            ConsumeInfo db = consumeInfoDao.getConsumeInfo(topicName, clientName, message.getId());
            if (db != null) {
                int consumeCount = db.getConsumeCount() + 1;
                consumeInfoDao.updateConsumeInfo(topicName, clientName, message.getId(), consumeCount);
            } else {
                ConsumeInfo consumeInfo = new ConsumeInfo();
                consumeInfo.setMsgId(message.getId());
                consumeInfo.setTopicId(topic.getId());
                consumeInfo.setSubscriberId(subscriber.getId());
                consumeInfo.setConsumeCount(1);
                consumeInfo.setCreateTime(new Date());
                consumeInfoDao.addConsumeInfo(consumeInfo);
            }
        }
    }

    private static String getCacheKey(String topicName, String clientName) {
        return topicName + "_" + clientName;
    }
}
