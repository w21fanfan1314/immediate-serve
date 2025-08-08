package com.leo.demos

import com.alibaba.fastjson.JSON
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.group.ChannelGroup
import io.netty.channel.group.DefaultChannelGroup
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.util.concurrent.GlobalEventExecutor

class ImmediateMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE)

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) throws Exception {
        def text = (webSocketFrame as TextWebSocketFrame).text()
        def channel = channelHandlerContext.channel()
        System.out.println("ImmediateMessageHandler receive message: " + text)

        // 如果不是json格式的数据，反馈客户端数据格式错误
        if (!isJson(text)) {
            sendSystemToChannel(channel,
                    new ImmediateResponseMessage(
                            code: ImmediateResponseCode.FAIL.code,
                            message: "json format error"))
            return
        }

        def message = JSON.parseObject(text, ImmediateReceiveMessage)
        if (!message.from) {
            if (message.to == 'getUserId') {
                sendSystemToChannel(channel,
                        new ImmediateResponseMessage(
                                code: ImmediateResponseCode.USER_ID_SUCCESS.code,
                                data: JSON.toJSONString(["user_id": channel.id().asShortText()]),
                                message: "success"))
            } else {
                sendSystemToChannel(channel,
                        new ImmediateResponseMessage(code: ImmediateResponseCode.FAIL.code, message: "from is null"))
            }
            return
        }

        if (!message.to) {
            sendSystemToChannel(channel,
                    new ImmediateResponseMessage(code: ImmediateResponseCode.FAIL.code, message: "to is null"))
            return
        }

        if (!message.data) {
            sendSystemToChannel(channel,
                    new ImmediateResponseMessage(code: ImmediateResponseCode.FAIL.code, message: "data is null"))
            return
        }

        switch (message.to) {
            case "broadcast":
                broadcast(channelHandlerContext, JSON.toJSONString(message))
                break
            default:
                def targetChannel = channels.find { it.id().asShortText() == message.to }
                if (targetChannel) {
                    sendSystemToChannel(targetChannel,
                            new ImmediateResponseMessage(
                                    code: ImmediateResponseCode.SUCCESS.code,
                                    data: JSON.toJSONString(message),
                                    message: "success"))
                }
        }
    }

    @Override
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace()
        ctx.close()
    }

    @Override
    void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx)
        channels.add(ctx.channel())
        broadcastSystem(
                ctx,
                new ImmediateResponseMessage(
                        code: ImmediateResponseCode.ONLINE.code,
                        data: ["user_id": ctx.channel().id().asShortText()],
                        message: "online"))
    }

    @Override
    void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx)
        channels.remove(ctx)
        broadcastSystem(
                ctx,
                new ImmediateResponseMessage(
                        code: ImmediateResponseCode.OFFLINE.code,
                        data: ["user_id": ctx.channel().id().asShortText()],
                        message: "offline"))
    }

    private static void broadcast(ChannelHandlerContext ctx, String message) {
        channels.forEach { channel ->
            if (channel != ctx.channel()) {
                channel.writeAndFlush(new TextWebSocketFrame(message))
            }
        }
    }

    private static void sendSystemToChannel(Channel channel, ImmediateResponseMessage message) {
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)))
    }

    private static void broadcastSystem(ChannelHandlerContext ctx, ImmediateResponseMessage message) {
        channels.forEach { channel ->
            if (channel != ctx.channel()) {
                sendSystemToChannel(channel, message)
            }
        }
    }

    private static boolean isJson(String text) {
        try {
            JSON.parseObject(text)
            JSON.parseObject(text, ImmediateReceiveMessage)
            return true
        } catch (Exception ignored) {
            return false
        }
    }
}
