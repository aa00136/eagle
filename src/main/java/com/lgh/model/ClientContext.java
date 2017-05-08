package com.lgh.model;

/**
 * Created by ligh on 2017/5/8.
 */
public class ClientContext {
    private static final ThreadLocal<PullContextData> CONTEXT = new ThreadLocal<PullContextData>();

    public static void put(PullContextData data) {
        CONTEXT.set(data);
    }

    public static PullContextData getAndRemove() {
        PullContextData data = CONTEXT.get();
        CONTEXT.remove();
        return data;
    }
}
