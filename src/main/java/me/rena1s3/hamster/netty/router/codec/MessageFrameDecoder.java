package me.rena1s3.hamster.netty.router.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.rena1s3.hamster.netty.router.serializer.MessageFrameSerializer;
import me.rena1s3.hamster.netty.router.serializer.impl.DefaultMessageFrameSerializer;

import java.util.List;

public class MessageFrameDecoder extends MessageToMessageDecoder<byte[]> {

    private final MessageFrameSerializer serializer;

    public MessageFrameDecoder() {
        this(new DefaultMessageFrameSerializer());
    }

    public MessageFrameDecoder(MessageFrameSerializer messageFrameSerializer) {
        this.serializer = messageFrameSerializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        out.add(serializer.deserialize(msg));
    }

}
