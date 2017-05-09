package com.lgh.dao;

import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import com.lgh.model.db.Topic;

import java.util.ArrayList;

/**
 * Created by ligh on 2017/5/6.
 */
public class TopicDao extends BaseDao{
    public Topic getTopicByName(String topicName) throws ServiceException {
        return get(
                "SELECT id, name, create_time, update_time FROM topic WHERE name=?",
                Topic.class, topicName);
    }

    public void createTopic(String topicName) throws ServiceException {
        String sqlPattern = "CREATE TABLE `%s` (" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT," +
                "  `content` varchar(1000) DEFAULT NULL," +
                "  `create_time` datetime NOT NULL," +
                "  `update_time` datetime NOT NULL," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        String sql = String.format(sqlPattern, topicName);
        update(sql, new ArrayList<Object>());
    }
}
