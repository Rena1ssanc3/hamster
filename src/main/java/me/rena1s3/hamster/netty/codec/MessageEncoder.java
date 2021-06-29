package me.rena1s3.hamster.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageEncoder extends MessageToByteEncoder<byte[]> {

    private static final byte[] MAGIC_NUMBER = new byte[]{0x58, 0x42, 0x17, 0x69};

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, byte[] o, ByteBuf byteBuf) throws Exception {
        if (o == null) {
            log.warn("Try to write null to channel.");
            throw new NullPointerException();
        } else if (o.length == 0) {
            log.warn("Try to write a empty byte array to channel.");
            throw new IllegalArgumentException();
        } else if (o.length > Integer.MAX_VALUE - 8) {
            log.warn("Byte array too large to send.");
            throw new IllegalArgumentException();
        }
        int frameLength = 8 + o.length;
        byteBuf.ensureWritable(frameLength)
                .writeBytes(MAGIC_NUMBER)
                .writeInt(o.length)
                .writeBytes(o);
    }

}
