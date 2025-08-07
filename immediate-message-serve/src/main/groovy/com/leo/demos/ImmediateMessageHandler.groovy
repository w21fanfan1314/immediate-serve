package com.leo.demos

import com.alibaba.fastjson.JSON
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.group.ChannelGroup
import io.netty.channel.group.DefaultChannelGroup
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.util.concurrent.GlobalEventExecutor

class ImmediateMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame>{
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) throws Exception {
        if (webSocketFrame instanceof WebSocketFrame) {
            def text = (webSocketFrame as TextWebSocketFrame).text()
            System.out.println("ImmediateMessageHandler receive message: " + text)
            // 如果不是json格式的数据，反馈客户端数据格式错误
            if (!JSON.isValid(text)) {
                channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(["code": 400, "message": "message format error"])))
                return
            }
            broadcast(channelHandlerContext, text)
        } else {
            throw new UnsupportedOperationException("unsupported message: " + webSocketFrame)
        }
    }

    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause)
        cause.printStackTrace()
        ctx.close()
    }

    @Override
    void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx)
        channels.add(ctx.channel())
    }

    @Override
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx)
        channels.remove(ctx)
    }

    private static void broadcast(ChannelHandlerContext ctx, String message) {
        channels.forEach { channel -> {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(new TextWebSocketFrame(message))
            }
        }}
    }
}
