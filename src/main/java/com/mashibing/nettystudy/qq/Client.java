package com.mashibing.nettystudy.qq;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;


/**
 * @Auther: TianHao
 * @Date: 2021/6/25 - 06 - 25 - 13:37
 * @Description: com.mashibing.nettystudy.qq
 * @version: 1.0
 */
public class Client {
    private Channel channel  = null;
    public void send(String msg){
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
        channel.writeAndFlush(buf);
    }
    public void closeConnect(){
        this.send("_bye_");
        System.out.println("已经退出");
    }
    public void connect(){
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();

        try {
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new MyClientChannelInitializer())
                    .connect("localhost", 8888);
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    if (!future.isSuccess()) {
                        System.out.println("not connected!");
                    } else {
                        System.out.println("connected!");
                         channel = future.channel();
                    }
                }
            });

            f.sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}
class MyClientChannelInitializer extends ChannelInitializer{

    protected void initChannel(Channel ch) throws Exception {
       ch.pipeline()
               .addLast(new TankMsgEncoder())
               .addLast(new MyClientHandler());
    }
}

class MyClientHandler extends  ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            String MsgAccepted = new String(bytes);
            System.out.println(MsgAccepted);
            ClientFrame.INSTANCE.updateText(MsgAccepted);
        } finally {
            if (buf != null) {
                ReferenceCountUtil.release(buf);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
//        ctx.writeAndFlush(buf);
        ctx.writeAndFlush(new TankMsg(5, 8));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //删除出现异常的客户端channle 并关闭连接,
        Server.clients.remove(ctx.channel());
        ctx.close();
    }
}