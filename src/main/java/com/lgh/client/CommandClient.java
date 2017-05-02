package com.lgh.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;

import com.lgh.constant.ClientConfig;
import com.lgh.constant.CommandCode;
import com.lgh.model.Command;
import com.lgh.model.CommandResponse;
import com.lgh.handler.comman.ClientCommandHandler;
import com.lgh.handler.decode.CommandDecoder;
import com.lgh.handler.encode.CommandEncoder;
import com.lgh.handler.heartbeat.ClientHeartBeatHandler;
import com.lgh.handler.heartbeat.ClientIdleStateTrigger;
import com.lgh.task.ReconnectTask;
import com.lgh.util.IDGenerator;
import com.lgh.util.SyncResponseFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;


public class CommandClient {
	private String host;
	private int port = 9000;
	private NioEventLoopGroup group;
	private Bootstrap bootstrap;
	private Channel channel;
	private int reConnectCount=0;
	private Map<Integer,SyncResponseFuture<Command>> futureMap=new HashMap<Integer,SyncResponseFuture<Command>>();

	public CommandClient(String host, int port) {
		this.host = host;
		this.port = port;
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(host, port)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast("IdleStateHandler", new IdleStateHandler(0, 0, 4));
						pipeline.addLast("CommandEncoder", new CommandEncoder());
						pipeline.addLast("CommandDecoder", new CommandDecoder());
						pipeline.addLast("ClientIdleStateTrigger",new ClientIdleStateTrigger());
						pipeline.addLast("ClientHeartbeatHandler",new ClientHeartBeatHandler());
						pipeline.addLast("ClientCommandHandler", new ClientCommandHandler(CommandClient.this,futureMap));
					}
				});
	}

	public synchronized void connectToServer() throws InterruptedException {
		if (channel != null && channel.isActive()) {
			return;
		}
		ChannelFuture future = bootstrap.connect(host, port);
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()){
					reConnectCount=0;
					System.out.println("connect to server success!");
					channel=future.channel();
				}
				else{
					reConnectCount++;
					if(reConnectCount> ClientConfig.MAX_RECONNECT_COUNT){
						System.out.println("faild to connect to server after 10 tries!");
						future.channel().close();
						return ;
					}
					System.out.println("faild to connect to server!");
					future.channel().eventLoop().schedule(new ReconnectTask(CommandClient.this),2,TimeUnit.SECONDS);
				}
			}
		});
	}
	
	public CommandResponse sendMessage(String message, boolean sync){
		Command cmd=new Command(IDGenerator.getRequestId(), CommandCode.CUSTOM_REQ,message);
		SyncResponseFuture<Command>future=new SyncResponseFuture<Command>();
		futureMap.put(cmd.getRequestId(), future);
		channel.writeAndFlush(cmd);
		
		Command resCmd=null;
		try {
			 resCmd=future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(resCmd==null){
			return null;
		}
		return new CommandResponse(resCmd.getResponseCode(), resCmd.getBody());
	}
	
	public void close(){
		channel.disconnect();
		channel.close();
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public NioEventLoopGroup getGroup() {
		return group;
	}

	public void setGroup(NioEventLoopGroup group) {
		this.group = group;
	}

	public static void main(String[] args) throws InterruptedException {
		/*ExecutorService executor=Executors.newFixedThreadPool(100);
		final CountDownLatch latch=new CountDownLatch(100);
		for(int i=0;i<100;i++){
			executor.submit(new Runnable() {
				public void run() {
					CommandClient client = new CommandClient("localhost", 8000);
					try {
						client.connectToServer();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName());
					client.close();
					
					latch.countDown();
				}
			});
		}
		latch.await();
		executor.shutdown();*/
		CommandClient client = new CommandClient("localhost", 8000);
		try {
			client.connectToServer();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Thread.sleep(10000);
		JsonObject json=new JsonObject();
		json.addProperty("name", "lgh");
		json.addProperty("sex", "man");
		CommandResponse response=client.sendMessage(json.toString(), true);
		System.out.println(response.getMessage());
	}
}
