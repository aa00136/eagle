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

    public void createTopic(String topic) throws ServiceException {
        String sql = "CREATE TABLE `test2` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `content` varchar(1000) DEFAULT NULL,\n" +
                "  `create_time` datetime NOT NULL,\n" +
                "  `update_time` datetime NOT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;\n";
        update(sql, new ArrayList<Object>());
    }
}
