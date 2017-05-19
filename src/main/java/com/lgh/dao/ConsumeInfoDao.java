package com.lgh.dao;

import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import com.lgh.model.db.ConsumeInfo;

/**
 * 消费记录
 *
 * @author ligh
 * @create 2017-05-19 13:49
 **/
public class ConsumeInfoDao extends BaseDao {
    public void addConsumeInfo(ConsumeInfo consumeInfo) throws ServiceException {
        add(consumeInfo);
    }

    public ConsumeInfo getConsumeInfo(String topicName, String clientName, Integer msgId) throws ServiceException {
        String sql = "SELECT c.* FROM consume_info as c " +
                "LEFT JOIN subscriber as s on c.subscriber_id = s.id " +
                "LEFT JOIN topic as t on c.topic_id = t.id " +
                "where c.msg_id=? and s.`name`=? and t.`name`=?";
        return get(sql, ConsumeInfo.class, msgId, clientName, topicName);
    }

    public void updateConsumeInfo(String topicName, String clientName, Integer msgId, Integer consumeCount) throws ServiceException {
        String sql = "UPDATE consume_info as c " +
                "LEFT JOIN subscriber as s on c.subscriber_id = s.id " +
                "LEFT JOIN topic as t on c.topic_id = t.id " +
                "set c.consume_count = ? " +
                "where c.msg_id=? and s.`name`=? and t.`name`=?";
        update(sql, consumeCount, msgId, clientName, topicName);
    }
}
