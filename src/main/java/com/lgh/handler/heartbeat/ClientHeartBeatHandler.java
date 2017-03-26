package com.lgh.handler.heartbeat;

import com.lgh.constant.CommandCode;
import com.lgh.dto.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter {
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof Command){
			Command cmd=(Command)msg;
			if(cmd.getCommandCode()== CommandCode.PING_RSP){
				System.out.println("get pong from "+ctx.channel().remoteAddress());
			}
		}
        ctx.fireChannelRead(msg);
    }
}
