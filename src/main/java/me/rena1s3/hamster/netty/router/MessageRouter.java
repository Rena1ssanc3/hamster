package me.rena1s3.hamster.netty.router;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import me.rena1s3.hamster.netty.registry.ChannelRegistry;
import me.rena1s3.hamster.netty.registry.MapChannelRegistry;

import java.net.SocketAddress;

@Slf4j
public class MessageRouter extends ChannelInboundHandlerAdapter {

    private final ChannelRegistry channelRegistry;

    public MessageRouter() {
        this(new MapChannelRegistry());
    }

    public MessageRouter(ChannelRegistry channelRegistry) {
        this.channelRegistry = channelRegistry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageFrame frame) {
            log.info("Start to resolve message frame.");
            String senderAddress = frame.getSenderAddress();
            log.info("The message is sent from {}.", senderAddress);
            ChannelHandlerContext senderContext = channelRegistry.getChannel(senderAddress);
            if (senderContext == null) {
                log.warn("Couldn't find the sender channel");
                throw new Exception("Sender mismatch!");
            }
            SocketAddress registeredSocketAddress = senderContext.channel().remoteAddress();
            SocketAddress currentSocketAddress = ctx.channel().remoteAddress();
            if (!currentSocketAddress.equals(registeredSocketAddress)) {
                log.warn("The message sender mismatch with current channel.");
                throw new Exception("Sender mismatch!");
            }
            String receiverAddress = frame.getReceiverAddress();
            log.info("The message is sent to {}.", receiverAddress);
            ChannelHandlerContext receiverContext = channelRegistry.getChannel(receiverAddress);
            if (receiverContext != null) {
                log.info("Successfully found the channel to receiver. Send to receivers.");
                receiverContext.writeAndFlush(frame);
            } else {
                log.warn("Couldn't found the channel to receiver. Drop the message frame.");
            }
            return;
        }
        super.channelRead(ctx, msg);
    }

}
