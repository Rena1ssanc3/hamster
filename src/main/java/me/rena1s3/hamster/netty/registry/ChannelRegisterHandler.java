package me.rena1s3.hamster.netty.registry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ChannelRegisterHandler extends ChannelOutboundHandlerAdapter {

    private final String address;

    public ChannelRegisterHandler(String address) {
        this.address = address;
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.info("Start to add listener for registration.");
        promise.addListener(
                future -> {
                    log.info("Start to register.");
                    if (future.isSuccess()) {
                        log.info("Start to write and flush address.");
                        ctx.writeAndFlush(address.getBytes(StandardCharsets.UTF_8)).sync();
                        log.info("Successfully write and flush address.");
                    } else {
                        log.warn("Failed to create channel. Escape registration.");
                    }
                }
        );
        log.info("Successfully add listener for registration.");
        super.connect(ctx, remoteAddress, localAddress, promise);
    }
}
