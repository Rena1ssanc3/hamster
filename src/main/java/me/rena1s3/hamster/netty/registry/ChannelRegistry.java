package me.rena1s3.hamster.netty.registry;

import io.netty.channel.ChannelHandlerContext;

public interface ChannelRegistry {

    void register(String address, ChannelHandlerContext context);

    void unregister(String address);

    ChannelHandlerContext getChannel(String address);

}
