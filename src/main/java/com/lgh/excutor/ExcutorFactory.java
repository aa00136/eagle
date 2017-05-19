package com.lgh.excutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcutorFactory {
	public static ExecutorService HEART_BEAT_CHECK=Executors.newFixedThreadPool(10);
    public static ExecutorService CONSUME_STATE = Executors.newFixedThreadPool(10);
}
