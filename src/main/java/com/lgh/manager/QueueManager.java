package com.lgh.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ligh on 2017/4/16.
 */
public class QueueManager {
    public static ConcurrentHashMap<String,ConcurrentLinkedQueue> topicQueue = new ConcurrentHashMap<String, ConcurrentLinkedQueue>(100);

}