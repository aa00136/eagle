package com.lgh.handler.decode;

import com.lgh.model.command.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.UnsupportedEncodingException;

public class CommandFrameDecoder extends LengthFieldBasedFrameDecoder {
    private int HEARD_LENGTH = 12;

    public CommandFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in.readableBytes() < HEARD_LENGTH) {
            System.out.println("heard length return");
            //in.resetReaderIndex();
            return null;
        }
        int requestId = in.readInt();
        short commandCode = in.readShort();
        byte responseCode = in.readByte();
        int bodyLength = in.readInt();
        byte extentionLength = in.readByte();

        System.out.println("requestId=" + requestId + " " + commandCode + " " + in.readableBytes());
        if (in.readableBytes() < bodyLength) {
            System.out.println(in.readableBytes() + " body length return " + bodyLength);
            in.resetReaderIndex();
            return null;
        }
        String body = readCommandContent(in, bodyLength);
        String extention = readCommandContent(in, extentionLength);

        Command cmd = new Command();
        cmd.setRequestId(requestId);
        cmd.setCommandCode(commandCode);
        cmd.setResponseCode(responseCode);
        cmd.setBody(body);
        cmd.setExtention(extention);

        return cmd;
    }

    private String readCommandContent(ByteBuf buffer, int len) throws UnsupportedEncodingException {
        byte[] body = new byte[len];
        buffer.readBytes(body);
        return new String(body, "utf-8");
    }
}
