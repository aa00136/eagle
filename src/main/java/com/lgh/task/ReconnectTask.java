package com.lgh.task;


import com.lgh.client.CommandClient;

public class ReconnectTask implements Runnable {
	private CommandClient client;
	
	public ReconnectTask(CommandClient client) {
		super();
		this.client = client;
	}
	public void run() {
		try {
			client.connectToServer();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
