package me.rena1s3.hamster.netty.router.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import me.rena1s3.hamster.netty.router.MessageFrame;
import me.rena1s3.hamster.netty.router.serializer.MessageFrameSerializer;
import me.rena1s3.hamster.netty.router.serializer.impl.DefaultMessageFrameSerializer;

import java.util.List;

public class MessageFrameCodec extends MessageToMessageCodec<byte[], MessageFrame> {

    private MessageFrameSerializer serializer;

    public MessageFrameCodec() {
        this(new DefaultMessageFrameSerializer());
    }

    public MessageFrameCodec(MessageFrameSerializer messageFrameSerializer) {
        this.serializer = messageFrameSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageFrame msg, List<Object> out) throws Exception {
        out.add(serializer.serialize(msg));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        out.add(serializer.deserialize(msg));
    }
}
