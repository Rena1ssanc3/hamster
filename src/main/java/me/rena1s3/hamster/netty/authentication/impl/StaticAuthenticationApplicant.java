package me.rena1s3.hamster.netty.authentication.impl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import me.rena1s3.hamster.netty.authentication.client.AuthenticationApplicant;

import java.nio.charset.StandardCharsets;

@Slf4j
public class StaticAuthenticationApplicant implements AuthenticationApplicant {
    @Override
    public void authenticate(ChannelHandlerContext context, ChannelPromise channelPromise) throws Exception {
        log.info("Start to add listen for authenticate.");
        channelPromise.addListener(
                future -> {
                    log.info("Start to authenticate.");
                    if (future.isSuccess()) {
                        context.writeAndFlush("Hamster".getBytes(StandardCharsets.UTF_8)).sync();
                        log.info("Successfully write and flush authenticate info.");
                    } else {
                        log.warn("Failed to create channel. Escape authentication.");
                    }
                }
        );
        log.info("Successfully add listener for authenticate.");
    }
}
