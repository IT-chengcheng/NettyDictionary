package com.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class TestEncoder  extends MessageToMessageEncoder{
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
        if(msg instanceof ByteBuf){
            ByteBuf byteBuf= (ByteBuf) msg;
            if(byteBuf instanceof UnpooledHeapByteBuf){
                byte[] array =byteBuf.array();
                System.out.println(new String(array,"utf-8"));
                ByteBuf byteBuf1 = Unpooled.copiedBuffer(array);
                out.add(byteBuf1);
            }else{
                long unsignedInt = byteBuf.getUnsignedInt(byteBuf.readerIndex());
                System.out.println(unsignedInt);
            }
        }
    }
}
