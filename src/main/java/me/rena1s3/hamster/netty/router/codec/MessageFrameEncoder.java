package me.rena1s3.hamster.netty.router.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import me.rena1s3.hamster.netty.router.MessageFrame;
import me.rena1s3.hamster.netty.router.serializer.MessageFrameSerializer;
import me.rena1s3.hamster.netty.router.serializer.impl.DefaultMessageFrameSerializer;

import java.util.List;

public class MessageFrameEncoder extends MessageToMessageEncoder<MessageFrame> {

    private final MessageFrameSerializer serializer;

    public MessageFrameEncoder() {
        this(new DefaultMessageFrameSerializer());
    }

    public MessageFrameEncoder(MessageFrameSerializer messageFrameSerializer) {
        this.serializer = messageFrameSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageFrame msg, List<Object> out) throws Exception {
        out.add(serializer.serialize(msg));
    }

}
