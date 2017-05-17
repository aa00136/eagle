package com.lgh.handler.command;

import com.huisa.common.exception.ServiceException;
import com.lgh.constant.CommandCode;
import com.lgh.model.command.*;
import com.lgh.server.ServerContext;
import com.lgh.service.CommandService;
import com.lgh.util.Log;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

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
            ServerContext.put(cmd);
            Log.SERVER_COMMAND.info("request=" + msg.toString());
            switch (cmd.getCommandCode()) {
                case CommandCode.CUSTOM_REQ:
                    Command resCmd = new Command(cmd.getRequestId(), CommandCode.CUSTOM_RSP, (byte) 0, "sever read message from " + ctx.channel().remoteAddress());
                    ctx.writeAndFlush(resCmd);

                    Log.SERVER_COMMAND.info("response=" + resCmd.toString());
                    break;
                case CommandCode.SUBSCRIBE_REQ:
                    SubscribeCommandResp subscribeResp = CommandService.excuteSubscribeCmd(cmd);
                    ctx.writeAndFlush(subscribeResp);

                    Log.SERVER_COMMAND.info("response=" + subscribeResp.toString());
                    break;
                case CommandCode.PULL_REQ:
                    PullCommandResp pullCommandResp = CommandService.excutePullCmd(cmd);
                    ctx.writeAndFlush(pullCommandResp);

                    Log.SERVER_COMMAND.info("response=" + pullCommandResp.toString());
                    break;
                case CommandCode.PUBLISH_REQ:
                    PublishCommandResp publishCommandResp = CommandService.excutePublishCmd(cmd);
                    ctx.writeAndFlush(publishCommandResp);

                    Log.SERVER_COMMAND.info("response=" + publishCommandResp.toString());
                    break;
                case CommandCode.PUBLISH_TOPIC_REQ:
                    PublishTopicCommandResp publishTopicCommandResp = CommandService.excutePublishTopicCmd(cmd);
                    ctx.writeAndFlush(publishTopicCommandResp);

                    Log.SERVER_COMMAND.info("response=" + publishTopicCommandResp.toString());
                    break;
                case CommandCode.PULL_ACK_REQ:
                    CommandService.excutePullAckCmd(cmd);
                    break;
                case CommandCode.UNSUBSCRIBE_REQ:
                    UnsubscribeCommandResp unsubscribeCommandResp = CommandService.excuteUnsubscribeCmd(cmd);
                    ctx.writeAndFlush(unsubscribeCommandResp);

                    Log.SERVER_COMMAND.info("response=" + unsubscribeCommandResp.toString());
                    break;
                default:
                    break;
            }
    	}
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (cause instanceof ServiceException) {
            Command requesCmd = ServerContext.getAndRemove();
            Command responseCmd = new Command(requesCmd.getRequestId(), requesCmd.getCommandCode(), (byte) 0, cause.getMessage());
            ctx.writeAndFlush(responseCmd);
            Log.SERVER_COMMAND.info("response=" + responseCmd.toString());
        }
        Log.SERVER_ERROR.error(cause.getMessage(), cause);
    }
}
