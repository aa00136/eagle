package com.lgh.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成器
 */
public class IDGenerator {

    private static AtomicInteger requestIdCounter = new AtomicInteger(1);

    /**
     * 获取唯一ID
     * 
     * @return
     */
    public static synchronized int getRequestId() {
        requestIdCounter.compareAndSet(Integer.MAX_VALUE, 1);
         
        return requestIdCounter.getAndIncrement();
    }
}