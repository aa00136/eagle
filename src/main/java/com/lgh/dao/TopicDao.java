package com.lgh.dao;

import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import com.lgh.model.db.Topic;

/**
 * Created by ligh on 2017/5/6.
 */
public class TopicDao extends BaseDao{
    public Topic getTopicByName(String topicName) throws ServiceException {
        return get(
                "SELECT id, name, create_time, update_time FROM topic WHERE name=?",
                Topic.class, topicName);
    }
}
