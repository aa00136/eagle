package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.TopicDao;
import com.lgh.model.db.Topic;

import java.util.Date;

/**
 * topic服务
 *
 * @author ligh
 * @create 2017-05-16 14:37
 **/
public class TopicService {
    private static TopicDao topicDao = new TopicDao();

    public static void createTopic(String topicName) throws ServiceException {
        Topic topic = new Topic();
        topic.setName(topicName);
        topic.setCreateTime(new Date());
        topicDao.addTopic(topic);
        topicDao.createQueue(topicName);
    }

    public static Topic getTopicByName(String topicName) throws ServiceException {
        return topicDao.getTopicByName(topicName);
    }

    public static Integer getQueueMaxMsgId(String topicName) throws ServiceException {
        return topicDao.getQueueMaxMsgId(topicName);
    }
}
