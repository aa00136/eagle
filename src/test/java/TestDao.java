import com.huisa.common.exception.ServiceException;
import com.lgh.dao.TopicDao;
import com.lgh.model.db.Topic;
import org.junit.*;

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
}
