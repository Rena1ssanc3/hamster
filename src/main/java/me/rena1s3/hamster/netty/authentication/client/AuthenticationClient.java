package me.rena1s3.hamster.netty.authentication.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;
import java.util.function.Supplier;

public class AuthenticationClient extends ChannelOutboundHandlerAdapter {

    private final AuthenticationApplicant authenticationApplicant;

    public AuthenticationClient(AuthenticationApplicant authenticationApplicant) {
        this.authenticationApplicant = authenticationApplicant;
    }

    public AuthenticationClient(Supplier<AuthenticationApplicant> supplier) {
        this.authenticationApplicant = supplier.get();
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        authenticationApplicant.authenticate(ctx, promise);
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

}
