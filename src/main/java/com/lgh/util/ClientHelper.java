package com.lgh.util;

import com.lgh.client.CommandClient;
import com.lgh.handler.MessageHandler;
import com.lgh.model.ClientContext;
import com.lgh.model.PullContextData;
import com.lgh.model.db.Message;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by ligh on 2017/5/8.
 */
public final class ClientHelper {
    private static int adminCounter = 0;

    public static void startPullTask(CommandClient client, String topicName, MessageHandler messageHandler, Integer messageCount, Integer period, Integer threadNumber) {
        if (StringUtils.isBlank(topicName)) {
            throw new IllegalArgumentException("topic is blank");
        }
        List<String> topicList = new ArrayList<String>();
        topicList.add(topicName);
        ScheduledExecutorService adminTaskExecutor = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactory() {
                    public Thread newThread(Runnable task) {
                        return new Thread(task, "pull-admin-task-" + adminCounter);
                    }
                }
        );
        adminTaskExecutor.scheduleWithFixedDelay(
                new PullAdminTask(client, topicList, messageHandler, messageCount, threadNumber),
                0, period, TimeUnit.MILLISECONDS);
    }

    private static class PullAdminTask implements Runnable {
        private CommandClient client;
        private List<String> topicList;
        private MessageHandler handler;
        private Integer messageCount;
        private Integer threadNumber;
        private ExecutorService pullTaskExecutor;

        public PullAdminTask(CommandClient client, List<String> topicList, MessageHandler handler, Integer messageCount, Integer threadNumber) {
            this.client = client;
            this.topicList = topicList;
            this.handler = handler;
            this.messageCount = messageCount;
            this.threadNumber = threadNumber;

            this.pullTaskExecutor = new ThreadPoolExecutor(threadNumber, threadNumber, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(topicList.size() * 4),
                    new ThreadFactory() {
                        private int count = 1;

                        public Thread newThread(Runnable task) {
                            return new Thread(task, "pull-subthread-task-" + adminCounter + "_" + count++);
                        }
                    }, new RejectedExecutionHandler() {
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    System.out.println("queue is full");
                }
            });
        }

        public void run() {
            pullTaskExecutor.submit(new PullTask(this.client, topicList.get(0), this.handler, this.messageCount));
        }
    }

    private static class PullTask implements Callable<String> {
        private CommandClient client;
        private String topicName;
        private MessageHandler handler;
        private Integer messageCount;

        public PullTask(CommandClient client, String topicName, MessageHandler handler, Integer messageCount) {
            this.client = client;
            this.topicName = topicName;
            this.handler = handler;
            this.messageCount = messageCount;
        }

        public String call() throws Exception {
            List<Message> messageList;
            while (true) {
                messageList = client.pull(topicName, messageCount, true);
                if (messageList == null || messageList.isEmpty()) {
                    break;
                }
                handler.handlerMessage(messageList);

                PullContextData pullContextData = new PullContextData(client, topicName, messageList.get(messageList.size() - 1).getId());
                ClientContext.put(pullContextData);
            }

            return topicName;
        }
    }
}
