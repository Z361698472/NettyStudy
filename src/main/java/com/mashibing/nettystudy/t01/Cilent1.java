package com.mashibing.nettystudy.t01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * @Auther: TianHao
 * @Date: 2021/6/22 - 06 - 22 - 9:14
 * @Description: com.mashibing.nettystudy.t01
 * @version: 1.0
 */
public class Cilent1 {

    public static void main(String[] args) {
        //netty自动是多线程的,想用单线程都不行,线程池,循环处理的事件池.eventLoopGroup
        EventLoopGroup gro = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        try {
            ChannelFuture f = b.group(gro)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer())
                    .connect("localhost", 8888);
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(!channelFuture.isSuccess()){
                        System.out.println("not connected!");
                    } else {
                        System.out.println("connected!");
                    }
                }
            });
            f.sync();//阻塞住,一直到出了结果为止
            System.out.println("1111");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
          gro.shutdownGracefully();//优雅的结束
        }
    }
}

