import com.lgh.client.CommandClient;
import com.lgh.handler.MessageHandlerImp;
import com.lgh.util.ClientHelper;

/**
 * Created by ligh on 2017/5/8.
 */
public class TestClient {
    @org.junit.Test
    public void testPullTask() throws InterruptedException {
        CommandClient client = new CommandClient("localhost", 8000);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ClientHelper.startPullTask(client, "test", new MessageHandlerImp(), 10, 1000, 1);

        while (true) {

        }
    }

    @org.junit.Test
    public void testPull() throws InterruptedException {
        CommandClient client = new CommandClient("localhost", 8000);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.pull("test", 5, true);
    }

    @org.junit.Test
    public void testPublish() throws InterruptedException {
        CommandClient client = new CommandClient("localhost", 8000);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //for (int i = 4002; i < 5000; i++) {
        client.publish("test", "hello lgh 500001", true);
        //}
    }

    @org.junit.Test
    public void testPullAck() throws InterruptedException {
        CommandClient client = new CommandClient("localhost", 8000);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.pullAck("test", 4);
    }

    @org.junit.Test
    public void testSubscribe() {
        CommandClient client = new CommandClient("localhost", 8000);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.subscribe("test", true);
    }

    @org.junit.Test
    public void testPublishTopic() {
        CommandClient client = new CommandClient("localhost", 8000);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.publishTopic("articcle_update", true);
    }
}
