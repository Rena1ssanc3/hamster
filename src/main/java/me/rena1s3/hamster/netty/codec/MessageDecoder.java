package me.rena1s3.hamster.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    // Determine current status. 0 = wait for magic number 1 = reading length 2 = reading content.
    private int status = 0;
    // Determine the match status of magic number.
    private int magic_match = 0;
    // When reading, determine the end of frame.
    private int length = 0;
    // Cache of frame.
    private final ByteBuf byteBuf = Unpooled.buffer();


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (status == 0) {
            if (byteBuf.readableBytes() > 0) {
                byte firstByte = byteBuf.readByte();
                switch (magic_match) {
                    case 0 -> {
                        if (firstByte == 0x58) {
                            magic_match = 1;
                            decode(channelHandlerContext, byteBuf, list);
                        }
                        return;
                    }
                    case 1 -> {
                        if (firstByte == 0x42) {
                            magic_match = 2;
                            decode(channelHandlerContext, byteBuf, list);
                        } else {
                            magic_match = 0;
                        }
                        return;
                    }
                    case 2 -> {
                        if (firstByte == 0x17) {
                            magic_match = 3;
                            decode(channelHandlerContext, byteBuf, list);
                        } else {
                            magic_match = 0;
                        }
                        return;
                    }
                    case 3 -> {
                        magic_match = 0;
                        if (firstByte == 0x69) {
                            status = 1;
                            decode(channelHandlerContext, byteBuf, list);
                        }
                        return;
                    }
                }
                log.error("Unexpected magic_match!");
            } else {
                return;
            }
        } else if (status == 1) {
            if (byteBuf.readableBytes() > 4) {
                length = byteBuf.readInt();
                status = 2;
                this.byteBuf.clear();
                this.byteBuf.ensureWritable(length);
                decode(channelHandlerContext, byteBuf, list);
            }
            return;
        } else if (status == 2) {
            int currentIndex = this.byteBuf.writerIndex();
            int readLength = length - currentIndex;
            if (readLength == 0) {
                byte[] res = new byte[length];
                this.byteBuf.readBytes(res);
                list.add(res);
                status = 0;
                decode(channelHandlerContext, byteBuf, list);
                return;
            }
            int readableLength = byteBuf.readableBytes();
            if (readableLength < readLength) {
                this.byteBuf.writeBytes(byteBuf);
            } else {
                this.byteBuf.writeBytes(byteBuf, readLength);
                byte[] res = new byte[length];
                this.byteBuf.readBytes(res);
                list.add(res);
                status = 0;
                decode(channelHandlerContext, byteBuf, list);
            }
            return;
        }
        log.error("Unexpected status!");
    }
}
