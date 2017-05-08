package com.lgh.handler.command;

import com.lgh.constant.CommandCode;
import com.lgh.model.command.*;
import com.lgh.model.db.Message;
import com.lgh.service.QueueService;
import com.lgh.service.SubscriberService;
import com.lgh.util.GsonSerializeUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    SubscriberService.addSubscriber(cmd);
                    SubscribeCommandResp subscribeResp = new SubscribeCommandResp(cmd.getRequestId(), (byte) 1, "subscribe success");
                    ctx.writeAndFlush(subscribeResp);
                    break;
                case CommandCode.PULL_REQ:
                    List<Message> messageList = QueueService.readMessage(cmd);
                    Map<String, Object> resultMap = new HashMap<String, Object>();
                    resultMap.put("data", messageList);
                    PullCommandResp pullCommandResp = new PullCommandResp(cmd.getRequestId(), (byte) 1, GsonSerializeUtil.toJson(resultMap));
                    ctx.writeAndFlush(pullCommandResp);
                    break;
                case CommandCode.PUBLISH_REQ:
                    QueueService.writeMessage(cmd);
                    PublishCommandResp publishCommandResp = new PublishCommandResp(cmd.getRequestId(), (byte) 1, "publish success");
                    ctx.writeAndFlush(publishCommandResp);
                    break;
                case CommandCode.PUBLISH_TOPIC_REQ:
                    QueueService.createTopic(cmd);
                    PublishTopicCommandResp publishTopicCommandResp = new PublishTopicCommandResp(cmd.getRequestId(), (byte) 1, "publish topic success");
                    ctx.writeAndFlush(publishTopicCommandResp);
                    break;
                case CommandCode.PULL_ACK_REQ:
                    QueueService.updateConsumeState(cmd);
                    break;
                default:
                    break;
            }
    	}
        ctx.fireChannelRead(msg);
    }
}
