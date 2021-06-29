package me.rena1s3.hamster.netty.starter;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import me.rena1s3.hamster.netty.authentication.client.AuthenticationApplicant;
import me.rena1s3.hamster.netty.authentication.client.AuthenticationClient;
import me.rena1s3.hamster.netty.authentication.impl.StaticAuthenticationApplicant;
import me.rena1s3.hamster.netty.codec.MessageDecoder;
import me.rena1s3.hamster.netty.codec.MessageEncoder;
import me.rena1s3.hamster.netty.registry.ChannelRegisterHandler;
import me.rena1s3.hamster.netty.router.codec.MessageFrameDecoder;
import me.rena1s3.hamster.netty.router.codec.MessageFrameEncoder;
import me.rena1s3.hamster.netty.router.serializer.MessageFrameSerializer;
import me.rena1s3.hamster.netty.router.serializer.impl.DefaultMessageFrameSerializer;

import java.util.UUID;

@Slf4j
public class HamsterClientStarter {

    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    private String address = UUID.randomUUID().toString();

    private AuthenticationApplicant authenticationApplicant = new StaticAuthenticationApplicant();

    private String hamsterServerHost = "127.0.0.1";

    private int hamsterServerPort = 16924;

    private MessageFrameSerializer messageFrameSerializer = new DefaultMessageFrameSerializer();

    public HamsterClientStarter setBossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
        return this;
    }

    public HamsterClientStarter setAddress(String address) {
        this.address = address;
        return this;
    }

    public HamsterClientStarter setAuthenticationApplicant(AuthenticationApplicant authenticationApplicant) {
        this.authenticationApplicant = authenticationApplicant;
        return this;
    }

    public HamsterClientStarter setHamsterServerHost(String hamsterServerHost) {
        this.hamsterServerHost = hamsterServerHost;
        return this;
    }

    public HamsterClientStarter setHamsterServerPort(int hamsterServerPort) {
        this.hamsterServerPort = hamsterServerPort;
        return this;
    }

    public HamsterClientStarter setMessageFrameSerializer(MessageFrameSerializer messageFrameSerializer) {
        this.messageFrameSerializer = messageFrameSerializer;
        return this;
    }

    public ChannelFuture start() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup);
        bootstrap.channel(NioSocketChannel.class);
        ChannelInitializer<? extends Channel> initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addFirst(new MessageFrameEncoder(messageFrameSerializer))
                        .addFirst(new MessageEncoder())
                        .addLast(new MessageDecoder())
                        .addLast(new ChannelRegisterHandler(address))
                        .addLast(new AuthenticationClient(authenticationApplicant))
                        .addLast(new MessageFrameDecoder(messageFrameSerializer))
                ;
            }
        };
        bootstrap.handler(initializer);
        ChannelFuture cf = bootstrap.connect(hamsterServerHost, hamsterServerPort);
        cf = cf.sync();
        cf.channel().closeFuture().addListener(
                future -> {
                    log.info("Start to shutdown the boss group.");
                    bossGroup.shutdownGracefully();
                    log.info("Successfully shutdown the boss group");
                }
        );
        return cf;

    }
}
