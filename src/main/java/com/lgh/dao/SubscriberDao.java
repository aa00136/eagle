package com.lgh.dao;

import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import com.lgh.model.db.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ligh on 2017/5/6.
 */
public class SubscriberDao extends BaseDao {
    public Subscriber getByClientNameAndTopicName(String clientName, String topicName) throws ServiceException {
        String sql = "select * from subscriber where name = ? and topic_name = ?";
        return get(sql, Subscriber.class, clientName, topicName);
    }

    public void updateMaxSendMsgId(String clientName, String topicName, Integer maxSendMsgId) throws ServiceException {
        String sql = "update subscriber set max_send_msg_id = ? where name = ? and topic_name = ?";
        update(sql, maxSendMsgId, clientName, topicName);
    }

    public void updateMinConsumeMsgId(String clientName, String topicName, Integer minConsumeMsgId) throws ServiceException {
        String sql = "update subscriber set min_consume_msg_id = ? where name = ? and topic_name = ?";
        update(sql, minConsumeMsgId, clientName, topicName);
    }

    public void deleteSubscriber(String clientName, String topicName) throws ServiceException {
        String sql = "delete from subscriber where name = ? and topic_name = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(clientName);
        params.add(topicName);
        delete(sql, params);
    }

    public void addSubscriber(Subscriber subscriber) throws ServiceException {
        add(subscriber);
    }
}
