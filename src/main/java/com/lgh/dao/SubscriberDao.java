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
    public Subscriber getByClientNameAndTopicId(String clientName, Integer topicId) throws ServiceException {
        String sql = "select * from subscriber where name = ? and topic_id = ?";
        return get(sql, Subscriber.class, clientName, topicId);
    }

    public void updateMaxSendMsgId(String clientName, Integer topicId, Integer maxSendMsgId) throws ServiceException {
        String sql = "update subscriber set max_send_msg_id = ? where name = ? and topic_id = ?";
        update(sql, maxSendMsgId, clientName, topicId);
    }

    public void updateMinConsumeMsgId(String clientName, Integer topicId, Integer minConsumeMsgId) throws ServiceException {
        String sql = "update subscriber set min_consume_msg_id = ? where name = ? and topic_Id = ?";
        update(sql, minConsumeMsgId, clientName, topicId);
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
