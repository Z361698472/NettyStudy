package com.mashibing.nettystudy.qq;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Auther: TianHao
 * @Date: 2021/6/25 - 06 - 25 - 17:27
 * @Description: com.mashibing.nettystudy.qq
 * @version: 1.0
 */
public class TankMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<8) return;//tcp的拆包和粘包问题
        //在tcp协议下,在网络底层,会拆成很多小包,接收方这边要把拆的包粘起来.
        //因为先写的x,所以先读X
        int x = in.readInt();
        int y = in.readInt();
        out.add(new TankMsg(x, y));

    }
}
