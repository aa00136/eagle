package com.lgh.handler.command;

import com.lgh.constant.CommandCode;
import com.lgh.model.command.*;
import com.lgh.model.db.Message;
import com.lgh.service.QueueService;
import com.lgh.service.SubscriberService;
import com.lgh.util.GsonSerializeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ServerCommandHandler extends ChannelInboundHandlerAdapter {

    public ServerCommandHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println(ctx.channel().remoteAddress()+" is active!");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	System.out.println(ctx.channel().remoteAddress()+" is inactive!");
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if(msg instanceof Command){
    		Command cmd=(Command)msg;
    		System.out.println(msg.toString());
    		switch (cmd.getCommandCode()) {
                case CommandCode.CUSTOM_REQ:
                    Command resCmd=new Command(cmd.getRequestId(),CommandCode.CUSTOM_RSP,"sever read message from "+ctx.channel().remoteAddress());
                    resCmd.setResponseCode((byte) 1);
                    ctx.writeAndFlush(resCmd);
                    break;
                case CommandCode.SUBSCRIBE_REQ:
                    SubscriberService.addSubscriber((SubscribeCommand) cmd);
                    SubscribeCommandResponse subscribeResp=new SubscribeCommandResponse(cmd.getRequestId(),(byte)1, "subscribe success");
                    ctx.writeAndFlush(subscribeResp);
                    break;
                case CommandCode.PULL_REQ:
                    Message message = QueueService.readMessage((PullCommand) cmd);
                    PullCommandResponse pullCommandResponse = new PullCommandResponse(cmd.getRequestId(),(byte)1, GsonSerializeUtil.toJsonObject(message).toString());
                    ctx.writeAndFlush(pullCommandResponse);
                    break;
                default:
                    break;
            }
    	}
        ctx.fireChannelRead(msg);
    }
}
