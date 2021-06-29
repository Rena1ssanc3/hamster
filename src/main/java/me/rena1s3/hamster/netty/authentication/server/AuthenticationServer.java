package me.rena1s3.hamster.netty.authentication.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class AuthenticationServer extends ChannelInboundHandlerAdapter {

    public AuthenticationServer(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationServer(Supplier<AuthenticationManager> supplier) {
        this.authenticationManager = supplier.get();
    }

    private final AuthenticationManager authenticationManager;

    boolean authenticated = false;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!authenticated) {
            if (msg instanceof byte[] bytes) {
                log.info("Try to authenticate.");
                try {
                    authenticationManager.authenticate(ctx, bytes);
                    log.info("Successfully authenticated.");
                    authenticated = true;
                } catch (Exception e) {
                    log.warn("Error occurred when authenticated. Shutdown the channel.", e);
                    ctx.close();
                }
            } else {
                log.warn("Received msg is not a byte array.Please check your pipeline. Shutdown the channel.");
                ctx.close();
            }
        } else {
            // Already authenticated.
            ctx.fireChannelRead(msg);
        }
    }
}
