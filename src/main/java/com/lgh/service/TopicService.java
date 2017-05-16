package com.lgh.service;

import com.huisa.common.exception.ServiceException;
import com.lgh.dao.TopicDao;
import com.lgh.model.command.Command;
import com.lgh.model.db.Topic;
import com.lgh.util.GsonSerializeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * topic服务
 *
 * @author ligh
 * @create 2017-05-16 14:37
 **/
public class TopicService {
    private static TopicDao topicDao = new TopicDao();

    public static void createTopic(Command publishTopicCommand) throws ServiceException {
        Map<String, Object> body = GsonSerializeUtil.fromJson(publishTopicCommand.getBody());
        String topicName = (String) body.get("topic_name");
        if (StringUtils.isBlank(topicName)) {
            throw new ServiceException(-1, "topic_name is blank");
        }

        Topic topic = new Topic();
        topic.setName(topicName);
        topic.setCreateTime(new Date());
        topicDao.addTopic(topic);
        topicDao.createQueue(topicName);
    }
}
