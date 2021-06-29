package me.rena1s3.hamster.netty.starter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.rena1s3.hamster.netty.authentication.impl.StaticAuthenticationManager;
import me.rena1s3.hamster.netty.authentication.server.AuthenticationServer;
import me.rena1s3.hamster.netty.codec.MessageDecoder;
import me.rena1s3.hamster.netty.codec.MessageEncoder;
import me.rena1s3.hamster.netty.registry.ChannelRegistry;
import me.rena1s3.hamster.netty.registry.MapChannelRegistry;
import me.rena1s3.hamster.netty.registry.ServerChannelRegisterHandler;
import me.rena1s3.hamster.netty.router.MessageRouter;
import me.rena1s3.hamster.netty.router.codec.MessageFrameDecoder;
import me.rena1s3.hamster.netty.router.codec.MessageFrameEncoder;
import me.rena1s3.hamster.netty.router.serializer.MessageFrameSerializer;
import me.rena1s3.hamster.netty.router.serializer.impl.DefaultMessageFrameSerializer;

public class HamsterServerStarter {

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private ChannelRegistry channelRegistry = new MapChannelRegistry();

    private int port = 16924;

    private String bindAddress = "0.0.0.0";

    private MessageFrameSerializer messageFrameSerializer = new DefaultMessageFrameSerializer();

    public HamsterServerStarter setBossGroup(EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
        return this;
    }

    public HamsterServerStarter setWorkerGroup(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
        return this;
    }

    public HamsterServerStarter setChannelRegistry(ChannelRegistry channelRegistry) {
        this.channelRegistry = channelRegistry;
        return this;
    }

    public HamsterServerStarter setPort(int port) {
        this.port = port;
        return this;
    }

    public HamsterServerStarter setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
        return this;
    }

    public HamsterServerStarter setMessageFrameSerializer(MessageFrameSerializer messageFrameSerializer) {
        this.messageFrameSerializer = messageFrameSerializer;
        return this;
    }

    public void start() throws Exception {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            ChannelInitializer<? extends Channel> initializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    channel.pipeline()
                            .addFirst(new MessageFrameEncoder(messageFrameSerializer))
                            .addFirst(new MessageEncoder())
                            .addLast(new MessageDecoder())
                            .addLast(new AuthenticationServer(StaticAuthenticationManager::new))
                            .addLast(new ServerChannelRegisterHandler(channelRegistry))
                            .addLast(new MessageFrameDecoder(messageFrameSerializer))
                            .addLast(new MessageRouter(channelRegistry))
                    ;
                }
            };
            bootstrap.childHandler(initializer);
            ChannelFuture channelFuture = bootstrap.bind(bindAddress, port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
