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
public class Cilent {

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

class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new ClientHandler());
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(),bytes);
        System.out.println(new String(bytes));

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       //chnnle 第一次连上可以用,写出一个字符串
        //netty 的ByteBuf 是直接访问操作系统的内存,Direct Memory 这样就跳过了CG.需要自己回收
        ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
        ctx.writeAndFlush(buf);//这个方法里面后面自动做了回收.
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();//一旦有异常,把当前通道关闭了

    }
}