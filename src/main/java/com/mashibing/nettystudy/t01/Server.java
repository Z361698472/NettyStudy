package com.mashibing.nettystudy.t01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.xml.bind.annotation.XmlElementDecl;
import java.io.IOException;


/**
 * @Auther: TianHao
 * @Date: 2021/6/24 - 06 - 24 - 8:30
 * @Description: com.mashibing.nettystudy.t01
 * @version: 1.0
 */
public class Server {
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);//单例模式

    public static void main(String[] args) throws IOException {
//        ServerSocket ss = new ServerSocket();
//        ss.bind(new InetSocketAddress(8888));
//        Socket s = ss.accept();
//        System.out.println("a client connect");
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //一个是接收连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        //一个是处理接收连接的io事件
        ServerBootstrap b = new ServerBootstrap();
        //已经连上的通道,调用一个回调的方法,childHandler
        try {
            ChannelFuture f = b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //初始化通道,初始化完成以后,才会调用work
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pl = socketChannel.pipeline();//责任链,可以在上面加处理器
                            pl.addLast(new ServerChildHandler());//在责任链上加上一个ChannelInboundHandlerAdapter
                        }
                    })
                    .bind(8888)
                    .sync();

            System.out.println("sever started");
            //这句话有了之后,阻塞如果有人调了close方法,如果没人掉close方法,close会永远等待
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}


class ServerChildHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());//把每一个通道都放在通道组里
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(),bytes);
            System.out.println(new String(bytes));

            //ctx.writeAndFlush(msg); 从单个客户端到多个客户端
            Server.clients.writeAndFlush(msg);

            //System.out.println(buf);
            //System.out.println(buf.refCnt());//看看buf的身上有几个引用
        } finally {

            if (buf != null){
                //ReferenceCountUtil.release(buf);
                //System.out.println(buf.refCnt());
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();//一旦有异常,把当前通道关闭了
    }
}