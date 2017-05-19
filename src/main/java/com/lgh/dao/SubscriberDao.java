package com.lgh.dao;

import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import com.lgh.model.db.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅关系存储
 *
 * @author ligh
 * @create 2017-04-16 20:44
 **/
public class SubscriberDao extends BaseDao {
    public Subscriber getByClientNameAndTopicName(String clientName, String topicName) throws ServiceException {
        String sql = "SELECT s.* FROM subscriber AS s " +
                "LEFT JOIN topic AS t ON s.topic_id = t.id " +
                "WHERE s.`name` = ? AND t.`name` = ?";
        return get(sql, Subscriber.class, clientName, topicName);
    }

    public void updateMaxSendMsgId(String clientName, String topicName, Integer maxSendMsgId) throws ServiceException {
        String sql = "UPDATE subscriber AS s " +
                "LEFT JOIN topic AS t ON s.topic_id = t.id " +
                "SET s.max_send_msg_id = ? " +
                "WHERE s.`name` = ? AND t.`name` = ?";
        update(sql, maxSendMsgId, clientName, topicName);
    }

    public void updateMinConsumeMsgId(String clientName, String topicName, Integer minConsumeMsgId) throws ServiceException {
        String sql = "UPDATE subscriber AS s " +
                "LEFT JOIN topic AS t ON s.topic_id = t.id " +
                "SET s.min_consume_msg_id = ? " +
                "WHERE s.`name` = ? AND t.`name` = ?";
        update(sql, minConsumeMsgId, clientName, topicName);
    }

    public void deleteSubscriber(String clientName, Integer topicId) throws ServiceException {
        String sql = "delete from subscriber where name = ? and topic_id = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(clientName);
        params.add(topicId);
        delete(sql, params);
    }

    public void addSubscriber(Subscriber subscriber) throws ServiceException {
        add(subscriber);
    }
}
