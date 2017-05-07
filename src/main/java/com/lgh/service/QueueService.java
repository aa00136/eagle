package com.lgh.service;

import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import com.lgh.model.command.PullCommand;
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
    private static BaseDao baseDao = new BaseDao();

    public static Message readMessage(PullCommand pullCommand) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(pullCommand.getBody());
        Subscriber subscriber = SubscriberService.getSubscriber((String) body.get("client_name"));
        String sql = String.format("select * from %s where id > ?", getQueueKey(subscriber));
        Message message = baseDao.get(sql, Message.class, subscriber.getMaxSendMsgId());
        return message;
    }

    private static String getQueueKey(Subscriber subscriber){
        return subscriber.getTopicName() + "_" + subscriber.getName();
    }
}