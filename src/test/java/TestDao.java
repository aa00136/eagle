import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import com.lgh.dao.MessageDao;
import com.lgh.dao.SubscriberDao;
import com.lgh.dao.TopicDao;
import com.lgh.model.db.Message;
import com.lgh.model.db.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ligh on 2017/5/6.
 */
public class TestDao {
    private TopicDao topicDao = new TopicDao();
    private MessageDao messageDao = new MessageDao();
    private SubscriberDao subscriberDao = new SubscriberDao();

    @org.junit.Test
    public void test1() throws ServiceException {
        Topic topic = topicDao.getTopicByName("lgh");
        System.out.println(topic.getId());
    }

    @org.junit.Test
    public void test2() throws ServiceException {
        //System.out.println(SubscriberService.getSubscriber("lgh").getTopicName());
    }

    @org.junit.Test
    public void test3() throws ServiceException {
        String sql = "CREATE TABLE `test2` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `content` varchar(1000) DEFAULT NULL,\n" +
                "  `create_time` datetime NOT NULL,\n" +
                "  `update_time` datetime NOT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;\n";
        new BaseDao().update(sql, new ArrayList<Object>());
    }

    @org.junit.Test
    public void test4() throws ServiceException {
        List<Message> messageList = messageDao.listMessageById("test", 0, 3);
        System.out.println(messageList.size());
    }

    @org.junit.Test
    public void test5() throws ServiceException {
        System.out.println(subscriberDao.getByClientNameAndTopicName("lgh", "test").getId());
    }

    @org.junit.Test
    public void test6() throws ServiceException {
        subscriberDao.updateMaxSendMsgId("lgh", "test", 110);
    }

    @org.junit.Test
    public void test7() throws ServiceException {
        subscriberDao.updateMinConsumeMsgId("lgh", "test", 110);
    }
}
