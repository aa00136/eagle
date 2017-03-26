package com.lgh.handler.heartbeat;

import com.lgh.excutor.ExcutorFactory;
import com.lgh.task.HeartBeatCheckTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter{
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			switch (state) {
				case WRITER_IDLE:
					handleWriterIdle(ctx);
					break;
				case READER_IDLE:
					handleReaderIdle(ctx);
					break;
				case ALL_IDLE:
					handleAllIdle(ctx);
					break;
				default:
					break;
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
    }
    private void handleAllIdle(ChannelHandlerContext ctx) {
    	ExcutorFactory.HEART_BEAT_CHECK.submit(new HeartBeatCheckTask(ctx));
    }
    
	private void handleReaderIdle(ChannelHandlerContext ctx) {
		
	}
	private void handleWriterIdle(ChannelHandlerContext ctx) {
		
	}
}
