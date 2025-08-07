package com.leo.demos

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

class ImmediateMessageServe {
    private int port

    ImmediateMessageServe(int port) {
        this.port = port
    }

    void start() {
        def boss = new NioEventLoopGroup()
        def worker = new NioEventLoopGroup()

        try {
            def bootstrap = new ServerBootstrap().group(boss, worker)
                .channel(NioServerSocketChannel)
                .childHandler(new ImmediateMessageServerInitializer())
            def channel = bootstrap.bind(port).sync()
            System.out.println("ImmediateMessageServer start at port: " + port)
            channel.channel().closeFuture().sync()
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            boss.shutdownGracefully()
            worker.shutdownGracefully()
        }
    }
}
