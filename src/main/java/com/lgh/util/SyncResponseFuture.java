package com.lgh.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncResponseFuture<T> implements Future<T> {
	private CountDownLatch latch=new CountDownLatch(1);
	private T response;
	
	public boolean isDone() {
		if(response!=null){
			return true;
		}
		return false;
	}

	public T get() throws InterruptedException, ExecutionException {
		latch.await();
		return response;
	}

	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if(latch.await(timeout, unit)){
			return response;
		}
		return null;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	public boolean isCancelled() {
		return false;
	}
	
	public void setResponse(T response){
		this.response=response;
		latch.countDown();
	}
}
