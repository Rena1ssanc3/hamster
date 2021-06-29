package me.rena1s3.hamster.netty.authentication.server;

import io.netty.channel.ChannelHandlerContext;

public interface AuthenticationManager {

    void authenticate(ChannelHandlerContext context, byte[] msg) throws Exception;

}
