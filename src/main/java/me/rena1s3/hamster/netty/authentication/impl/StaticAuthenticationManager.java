package me.rena1s3.hamster.netty.authentication.impl;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import me.rena1s3.hamster.netty.authentication.server.AuthenticationManager;

@Slf4j
public class StaticAuthenticationManager implements AuthenticationManager {
    @Override
    public void authenticate(ChannelHandlerContext context, byte[] msg) throws Exception {
        log.info("Start to authenticate.");
        if (!"Hamster".equals(new String(msg))) {
            log.warn("Bytes received are not match.");
            throw new Exception("Failed to authenticated");
        }
        log.info("Successfully authenticate.");
    }
}
