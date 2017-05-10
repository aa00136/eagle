package com.lgh.client;

import com.google.gson.JsonObject;
import com.lgh.constant.CommandCode;
import com.lgh.handler.command.ClientCommandHandler;
import com.lgh.handler.decode.CommandReplayingDecoder;
import com.lgh.handler.encode.CommandEncoder;
import com.lgh.model.ClientContext;
import com.lgh.model.PullContextData;
import com.lgh.model.command.Command;
import com.lgh.model.command.CommandResp;
import com.lgh.model.db.Message;
import com.lgh.task.ReconnectTask;
import com.lgh.util.GsonSerializeUtil;
import com.lgh.util.IDGenerator;
import com.lgh.util.Log;
import com.lgh.util.SyncResponseFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class CommandClient {
    private ClientConfig clientConfig;
    private NioEventLoopGroup group;
	private Bootstrap bootstrap;
	private Channel channel;
	private int reConnectCount=0;
	private Map<Integer,SyncResponseFuture<Command>> futureMap=new HashMap<Integer,SyncResponseFuture<Command>>();

    public CommandClient(ClientConfig clientConfig) {
        if (clientConfig == null) {
            throw new IllegalArgumentException("client config is null");
        }
        this.clientConfig = clientConfig;
        group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress(clientConfig.getHost(), clientConfig.getPort())
                .handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
                        //pipeline.addLast("IdleStateHandler", new IdleStateHandler(0, 0, 4));
                        pipeline.addLast("CommandEncoder", new CommandEncoder());
                        pipeline.addLast("CommandDecoder", new CommandReplayingDecoder());
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
        ChannelFuture future = bootstrap.connect(clientConfig.getHost(), clientConfig.getPort());
        future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()){
					reConnectCount=0;
					System.out.println("connect to server success!");
                    Log.CLIENT_STARTUP.info("connect to server success!");
                    channel=future.channel();
				}
				else{
					reConnectCount++;
					if(reConnectCount> ClientConfig.MAX_RECONNECT_COUNT){
						System.out.println("faild to connect to server after 10 tries!");
                        Log.CLIENT_STARTUP.info("faild to connect to server after 10 tries!");
                        future.channel().close();
						return ;
					}
					System.out.println("faild to connect to server!");
                    Log.CLIENT_STARTUP.info("faild to connect to server!");
                    future.channel().eventLoop().schedule(new ReconnectTask(CommandClient.this),2,TimeUnit.SECONDS);
				}
			}
		});
	}

    public CommandResp sendMessage(String message, boolean sync) {
        Command cmd=new Command(IDGenerator.getRequestId(), CommandCode.CUSTOM_REQ,message);

        return sendCommand(cmd);
    }

    public CommandResp subscribe(String topicName, boolean sync) {
        JsonObject json = new JsonObject();
        json.addProperty("topic_name", topicName);
        json.addProperty("client_name", clientConfig.getClientName());
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.SUBSCRIBE_REQ, json.toString());

        return sendCommand(cmd);
    }

    public List<Message> pull(String topicName, Integer messageCount, boolean sync) {
        sendPrePullAck();

        JsonObject json = new JsonObject();
        json.addProperty("topic_name", topicName);
        json.addProperty("client_name", clientConfig.getClientName());
        json.addProperty("limit", messageCount);
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.PULL_REQ, json.toString());
        CommandResp commandResp = sendCommand(cmd);

        return praseMessage(commandResp.getMessage());
    }

    public CommandResp publish(String topicName, String message, boolean sync) {
        JsonObject json = new JsonObject();
        json.addProperty("topic_name", topicName);
        json.addProperty("content", message);
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.PUBLISH_REQ, json.toString());

        return sendCommand(cmd);
    }

    public CommandResp publishTopic(String topic, boolean sync) {
        JsonObject json = new JsonObject();
        json.addProperty("topic_name", topic);
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.PUBLISH_TOPIC_REQ, json.toString());

        return sendCommand(cmd);
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
        json.addProperty("client_name", clientConfig.getClientName());
        json.addProperty("msg_id", messageId);
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.PULL_ACK_REQ, json.toString());
        channel.writeAndFlush(cmd);
        Log.CLIENT_COMMAND.info("request=" + cmd.toString());
    }

    public CommandResp unsubscribe(String topicName, boolean sync) {
        JsonObject json = new JsonObject();
        json.addProperty("topic_name", topicName);
        json.addProperty("client_name", clientConfig.getClientName());
        Command cmd = new Command(IDGenerator.getRequestId(), CommandCode.UNSUBSCRIBE_REQ, json.toString());

        return sendCommand(cmd);
    }

    private CommandResp sendCommand(Command cmd) {
        SyncResponseFuture<Command> future = new SyncResponseFuture<Command>();
        futureMap.put(cmd.getRequestId(), future);
        channel.writeAndFlush(cmd);
        Log.CLIENT_COMMAND.info("request=" + cmd.toString());

        Command resCmd = null;
        try {
            resCmd = future.get();
            Log.CLIENT_COMMAND.info("response=" + resCmd.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (resCmd == null || resCmd.getResponseCode() != 1) {
            return null;
        }
        return new CommandResp(resCmd.getResponseCode(), resCmd.getBody());
    }

    private List<Message> praseMessage(String messageBody) {
        if (messageBody == null) {
            return new ArrayList<Message>();
        }
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
        ClientConfig clientConfig = new ClientConfig("localhost", 8000, "lgh");
        CommandClient client = new CommandClient(clientConfig);
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
