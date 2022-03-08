package com.nico.net;

import com.alibaba.fastjson.JSON;
import com.nico.common.RespWapper;
import com.nico.dispatch.Dispatcher;
import com.nico.model.User;
import com.nico.net.config.ChannelUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author liuli
 */
@Slf4j
@Component
public class BusinessHandler {

    private WebSocketServerHandshaker handshaker;
    // 注意，这条地址别被误导了，其实这里填写什么都无所谓，WS协议消息的接收不受这里控制
    private static final String WEB_SOCKET_URL = "";//ws://localhost:8888/websocket";

    @Autowired
    private Dispatcher dispatcher;


    public void onChannelActive(ChannelHandlerContext ctx) {


    }

    public void onChannelInactive(ChannelHandlerContext ctx) {
        ChannelUtil.group.remove(ctx.channel());
    }

    public void onChannelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    protected void onChannelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) { //处理websocket连接业务
            handWebsocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if (!(frame instanceof TextWebSocketFrame)) {
            log.info("目前我们不支持二进制消息");
            throw new RuntimeException("【" + this.getClass().getName() + "】不支持消息");
        }
        String request = ((TextWebSocketFrame) frame).text();
        log.info("服务端收到客户端的消息====>>>" + request);
        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + " " + ctx.channel().id() + " ===>>> " + request);
        //群发，服务端向每个连接上来的客户端群发消息
        //	NettyConfig.group.writeAndFlush(tws);
        //给一会话的用户发送消息
        Channel channel = ChannelUtil.group.find(ctx.channel().id());
        channel.writeAndFlush(tws);
    }

    private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.decoderResult().isSuccess() || !("websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, null, false);
        handshaker = wsFactory.newHandshaker(req);
        ChannelUtil.group.add(ctx.channel());
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req,
                                  DefaultFullHttpResponse resp) {
        RespWapper respWapper = dispatcher.dispatch(req);

        if (respWapper.getStatus().code() != HttpResponseStatus.OK.code()) {
            ByteBuf buf = Unpooled.copiedBuffer(respWapper.getStatus().toString(), CharsetUtil.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
        } else {
            ByteBuf buf = Unpooled.copiedBuffer(respWapper.getRespBody(), CharsetUtil.UTF_8);
            resp.content().writeBytes(buf);
            buf.release();
        }
        ctx.channel().writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);

    }
}
