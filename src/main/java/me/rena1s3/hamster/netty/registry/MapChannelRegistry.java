package me.rena1s3.hamster.netty.registry;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MapChannelRegistry implements ChannelRegistry {

    private final Map<String, ChannelHandlerContext> map = new HashMap<>();

    @Override
    public void register(String address, ChannelHandlerContext context) {
        log.info("Receive registration from {}.", address);
        map.put(address, context);
    }

    @Override
    public void unregister(String address) {
        log.info("Receive unregister from {}.", address);
        map.remove(address);
    }

    @Override
    public ChannelHandlerContext getChannel(String address) {
        return map.get(address);
    }
}
