package com.lgh.client;

import com.google.gson.JsonObject;
import com.lgh.constant.ClientConfig;
import com.lgh.constant.CommandCode;
import com.lgh.handler.command.ClientCommandHandler;
import com.lgh.handler.decode.CommandDecoder;
import com.lgh.handler.encode.CommandEncoder;
import com.lgh.model.ClientContext;
import com.lgh.model.PullContextData;
import com.lgh.model.command.Command;
import com.lgh.model.command.CommandResp;
import com.lgh.model.db.Message;
import com.lgh.task.ReconnectTask;
import com.lgh.util.GsonSerializeUtil;
import com.lgh.util.IDGenerator;
import com.lgh.util.SyncResponseFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


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
                        //pipeline.addLast("IdleStateHandler", new IdleStateHandler(0, 0, 4));
                        pipeline.addLast("CommandEncoder", new CommandEncoder());
						pipeline.addLast("CommandDecoder", new CommandDecoder());
                        //pipeline.addLast("ClientIdleStateTrigger",new ClientIdleStateTrigger());
                        //pipeline.addLast("ClientHeartbeatHandler",new ClientHeartBeatHandler());
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

    public CommandResp sendMessage(String message, boolean sync) {
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
        return new CommandResp(resCmd.getResponseCode(), resCmd.getBody());
    }

    public CommandResp subscribe(String message, boolean sync) {
        Command cmd=new Command(IDGenerator.getRequestId(), CommandCode.SUBSCRIBE_REQ,message);
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
        return new CommandResp(resCmd.getResponseCode(), resCmd.getBody());
    }

    public List<Message> pull(String topicName, Integer messageCount, boolean sync) {
        //sendPrePullAck();

        JsonObject json = new JsonObject();
        json.addProperty("topic_name", topicName);
        json.addProperty("client_name", "lgh");
        json.addProperty("limit", messageCount);
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.PULL_REQ, json.toString());
        SyncResponseFuture<Command> future = new SyncResponseFuture<Command>();
        futureMap.put(cmd.getRequestId(), future);
        channel.writeAndFlush(cmd);

        Command resCmd = null;
        try {
            resCmd = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (resCmd == null) {
            return null;
        }
        if (resCmd.getResponseCode() != 1) {
            return null;
        }

        return praseMessage(resCmd.getBody());
    }

    public CommandResp publish(String topicName, String message, boolean sync) {
        JsonObject json = new JsonObject();
        json.addProperty("topic_name", topicName);
        json.addProperty("content", message);
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.PUBLISH_REQ, json.toString());
        SyncResponseFuture<Command> future = new SyncResponseFuture<Command>();
        futureMap.put(cmd.getRequestId(), future);
        channel.writeAndFlush(cmd);

        Command resCmd = null;
        try {
            resCmd = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (resCmd == null) {
            return null;
        }
        return new CommandResp(resCmd.getResponseCode(), resCmd.getBody());
    }

    public CommandResp publishTopic(String topic, boolean sync) {
        JsonObject json = new JsonObject();
        json.addProperty("topic_name", topic);
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.PUBLISH_TOPIC_REQ, json.toString());
        SyncResponseFuture<Command> future = new SyncResponseFuture<Command>();
        futureMap.put(cmd.getRequestId(), future);
        channel.writeAndFlush(cmd);

        Command resCmd = null;
        try {
            resCmd = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (resCmd == null) {
            return null;
        }
        return new CommandResp(resCmd.getResponseCode(), resCmd.getBody());
    }

    public void sendPrePullAck() {
        PullContextData contextData = ClientContext.getAndRemove();
        if (contextData != null) {
            contextData.getClient().pullAck(contextData.getTopicName(), contextData.getLastMsgId());
        }
    }

    public void pullAck(String topicName, Integer messageId) {
        JsonObject json = new JsonObject();
        json.addProperty("topic_name", topicName);
        json.addProperty("client_name", "lgh");
        json.addProperty("msg_id", messageId);
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.PULL_ACK_REQ, json.toString());
        channel.writeAndFlush(cmd);
    }

    private List<Message> praseMessage(String messageBody) {
        CommandResp commandResp = GsonSerializeUtil.fromJson(messageBody, CommandResp.class);
        return commandResp.getData();
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
		JsonObject json=new JsonObject();
		json.addProperty("client_name", "lgh");
        //json.addProperty("content", "test");
        //CommandResp response=client.publish("hello lgh2", true);
        List<Message> messageList = client.pull("test", 3, true);
        //CommandResp response=client.publishTopic("test2", true);
        System.out.println(messageList.get(0).getContent());
    }
}
