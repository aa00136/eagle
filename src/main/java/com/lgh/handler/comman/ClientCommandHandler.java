package com.lgh.handler.comman;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.lgh.client.CommandClient;
import com.lgh.constant.CommandCode;
import com.lgh.model.Command;
import com.lgh.task.ReconnectTask;
import com.lgh.util.SyncResponseFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


@Sharable
public class ClientCommandHandler extends ChannelInboundHandlerAdapter {
	private CommandClient client;
	private Map<Integer, SyncResponseFuture<Command>> futureMap = new HashMap<Integer, SyncResponseFuture<Command>>();

	public ClientCommandHandler(CommandClient client,Map<Integer, SyncResponseFuture<Command>> futureMap) {
		this.client = client;
		this.futureMap=futureMap;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("connect to:" + ctx.channel().remoteAddress());
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().eventLoop().schedule(new ReconnectTask(client),2,TimeUnit.SECONDS);
		ctx.fireChannelInactive();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof Command) {
			Command cmd = (Command) msg;
			if (cmd.getCommandCode() == CommandCode.CUSTOM_RSP) {
				SyncResponseFuture<Command>future=futureMap.remove(cmd.getRequestId());
				future.setResponse(cmd);
			}
		}
		ctx.fireChannelRead(msg);
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		cause.printStackTrace();
    }
}