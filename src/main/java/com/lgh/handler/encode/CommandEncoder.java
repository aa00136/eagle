package com.lgh.handler.encode;

import com.lgh.dto.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommandEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
			if(msg instanceof Command){
				Command cmd=(Command)msg;
				//out.alloc().buffer(12+cmd.getBodyLength()+cmd.getExtentionLength());
				out.writeInt(cmd.getRequestId());
				out.writeShort(cmd.getCommandCode());
				out.writeByte(cmd.getResponseCode());
				out.writeInt(cmd.getBodyLength());
				out.writeByte(cmd.getExtentionLength());
				if(cmd.getBodyLength()>0){
					out.writeBytes(cmd.getBody().getBytes("utf-8"));
				}
				if(cmd.getExtentionLength()>0){
					out.writeBytes(cmd.getExtention().getBytes("utf-8"));
				}
				//ReferenceCountUtil.releaseLater(out);
			}
	}
}
