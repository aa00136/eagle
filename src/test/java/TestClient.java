import com.lgh.client.CommandClient;
import com.lgh.handler.MessageHandler;
import com.lgh.handler.MessageHandlerImp;
import com.lgh.util.ClientHelper;

/**
 * Created by ligh on 2017/5/8.
 */
public class TestClient {
    @org.junit.Test
    public void testPull() throws InterruptedException {
        CommandClient client = new CommandClient("localhost", 8000);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MessageHandler messageHandler = new MessageHandlerImp();
        ClientHelper.startPullTask(client, "test", messageHandler, 2, 100, 1);

        while (true) {

        }
    }

    @org.junit.Test
    public void testPublish() throws InterruptedException {
        CommandClient client = new CommandClient("localhost", 8000);
        try {
            client.connectToServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 1000; i++) {
            client.publish("test", "hello lgh " + i, true);
        }
    }
}
