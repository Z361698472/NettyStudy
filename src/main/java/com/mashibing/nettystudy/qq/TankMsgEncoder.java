package com.mashibing.nettystudy.qq;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;



/**
 * @Auther: TianHao
 * @Date: 2021/6/25 - 06 - 25 - 17:15
 * @Description: com.mashibing.nettystudy.qq
 * @version: 1.0
 */
public class TankMsgEncoder extends MessageToByteEncoder<TankMsg> {
    //把一个消息转换成一个字节
    @Override
    protected void encode(ChannelHandlerContext ctx, TankMsg msg, ByteBuf buf) throws Exception {
        buf.writeInt(msg.x);
        buf.writeInt(msg.y);
    }
}
