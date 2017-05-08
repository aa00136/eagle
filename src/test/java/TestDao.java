import com.huisa.common.database.BaseDao;
import com.huisa.common.exception.ServiceException;
import com.lgh.dao.TopicDao;
import com.lgh.model.db.Topic;
import com.lgh.service.SubscriberService;

import java.util.ArrayList;

/**
 * Created by ligh on 2017/5/6.
 */
public class TestDao {
    private TopicDao topicDao = new TopicDao();

    @org.junit.Test
    public void test1() throws ServiceException {
        Topic topic = topicDao.getTopicByName("lgh");
        System.out.println(topic.getId());
    }

    @org.junit.Test
    public void test2() throws ServiceException {
        System.out.println(SubscriberService.getSubscriber("lgh").getTopicName());
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
}
