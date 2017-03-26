package com.lgh.excutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcutorFactory {
	public static ExecutorService HEART_BEAT_CHECK=Executors.newFixedThreadPool(10);
}
