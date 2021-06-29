package me.rena1s3.hamster.netty.registry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerChannelRegisterHandler extends ChannelInboundHandlerAdapter {

    private final ChannelRegistry channelRegistry;

    private boolean registered = false;

    private String address;

    public ServerChannelRegisterHandler(ChannelRegistry channelRegistry) {
        this.channelRegistry = channelRegistry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!registered) {
            log.info("Start to register for channel.");
            if (msg instanceof byte[] bytes) {
                address = new String(bytes);
                log.info("The address of current channel is {}", address);
                channelRegistry.register(address, ctx);
                log.info("Successfully registered.");
                this.registered = true;
            } else {
                log.warn("The register info is not a byte array. Shutdown the channel.");
                ctx.close();
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Start to unregister for channel. Address : {}.", address);
        channelRegistry.unregister(address);
        log.info("Successfully unregister for the channel");
        super.channelInactive(ctx);
    }


}
