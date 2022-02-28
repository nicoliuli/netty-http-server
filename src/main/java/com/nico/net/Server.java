package com.nico.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * netty引擎
 *
 * @author liuli
 */
@Slf4j
@Component
public class Server implements CommandLineRunner {


	@Autowired
	private HttpChannelHandler httpChannelHandler;
    @Value("${server.port}")
    private Integer port;
    @Value("${server.boss-group-nThreads}")
    private Integer bossGroupThreads;
    @Value("${server.work-group-nThreads}")
    private Integer workGroupThreads;


    private void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupThreads);
        EventLoopGroup workGroup = new NioEventLoopGroup(workGroupThreads);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(httpChannelHandler);

            if(Objects.isNull(port)){
                port = 8080;
            }
            ChannelFuture channelFuture = b.bind(port).sync().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    log.info("netty-http-server started on port(s): {} (http)",port);
                }
            });

            Channel channel = channelFuture.channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        start();
    }
}
