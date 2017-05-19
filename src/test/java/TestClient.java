import com.lgh.client.ClientConfig;
import com.lgh.client.CommandClient;
import com.lgh.handler.MessageHandlerImp;
import com.lgh.util.ClientHelper;

/**
 * Created by ligh on 2017/5/8.
 */
public class TestClient {
    private static ClientConfig clientConfig = new ClientConfig("localhost", 8000, "lgh");

    @org.junit.Test
    public void testPullTask() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        client.subscribe("test", true);
        ClientHelper.startPullTask(client, "test", new MessageHandlerImp(), 10, 100, 1);

        while (true) {

        }
    }

    @org.junit.Test
    public void testPullTask2() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        client.subscribe("test", true);
        ClientHelper.startPullTask(client, "test", new MessageHandlerImp(), 10, 100, 1);

        while (true) {

        }
    }

    @org.junit.Test
    public void testPullTask3() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        client.subscribe("test", true);
        ClientHelper.startPullTask(client, "test", new MessageHandlerImp(), 10, 100, 1);

        while (true) {

        }
    }

    @org.junit.Test
    public void testPull() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            client.pull("test", 5, true);
        }

        while (true) {

        }
    }

    @org.junit.Test
    public void testPublish() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        for (int i = 0; i < 5000; i++) {
            client.publish("test", "hello lgh " + i, true);
        }
    }

    @org.junit.Test
    public void testPublish2() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 10001; i < 13000; i++) {
            client.publish("test", "hello lgh " + i, true);
        }
    }

    @org.junit.Test
    public void testPullAck() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        client.pullAck("test", 4);
    }

    @org.junit.Test
    public void testSubscribe() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        client.subscribe("test", true);
    }

    @org.junit.Test
    public void testPublishTopic() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        client.publishTopic("articcle_update", true);
    }

    @org.junit.Test
    public void testUnsubscribe() throws InterruptedException {
        CommandClient client = new CommandClient(clientConfig);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);
        client.unsubscribe("test", true);
    }
}
