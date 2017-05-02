package com.lgh.handler.comman;

import com.lgh.constant.CommandCode;
import com.lgh.model.Command;
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
    		if(cmd.getCommandCode()== CommandCode.CUSTOM_REQ){
    			System.out.println(msg.toString());
    			Command resCmd=new Command(cmd.getRequestId(),CommandCode.CUSTOM_RSP,"sever read message from "+ctx.channel().remoteAddress());
    			resCmd.setResponseCode((byte) 1);
    			ctx.writeAndFlush(resCmd);
    		}
    	}
        ctx.fireChannelRead(msg);
    }
}
