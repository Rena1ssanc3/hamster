package me.rena1s3.hamster.netty.authentication.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public interface AuthenticationApplicant {

    void authenticate(ChannelHandlerContext context, ChannelPromise channelPromise) throws Exception;

}
