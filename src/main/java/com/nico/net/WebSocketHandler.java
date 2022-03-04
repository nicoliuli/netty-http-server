package com.nico.net;

import io.netty.channel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liuli
 */
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {


    @Autowired
    private BusinessHandler businessHandler;


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        businessHandler.onChannelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        businessHandler.onChannelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        businessHandler.onChannelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        businessHandler.onExceptionCaught(ctx,cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        businessHandler.onChannelRead0(ctx,msg);
    }

}
