package com.lgh.handler.decode;

import com.lgh.model.command.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class CommandReplayingDecoder extends ReplayingDecoder<Void> {
    private int HEARD_LENGTH = 12;

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEARD_LENGTH) {
            System.out.println("heard length return");
            return;
        }
        int requestId = in.readInt();
        short commandCode = in.readShort();
        byte responseCode = in.readByte();
        int bodyLength = in.readInt();
        byte extentionLength = in.readByte();

        in.markReaderIndex();
        //System.out.println("requestId=" + requestId + " " + commandCode + " " + in.readableBytes());
        if (in.readableBytes() < bodyLength) {
            System.out.println(in.readableBytes() + " body length return " + bodyLength);
            in.resetReaderIndex();
            return;
        }
        String body = readCommandContent(in, bodyLength);
        String extention = readCommandContent(in, extentionLength);

        Command cmd = new Command();
        cmd.setRequestId(requestId);
        cmd.setCommandCode(commandCode);
        cmd.setResponseCode(responseCode);
        cmd.setBody(body);
        cmd.setExtention(extention);

        out.add(cmd);
    }

    private String readCommandContent(ByteBuf buffer, int len) throws UnsupportedEncodingException {
        byte[] body = new byte[len];
        buffer.readBytes(body);
        return new String(body, "utf-8");
    }

}
