package com.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//客户端业务处理类
public class NettyClientHendler extends ChannelInboundHandlerAdapter{
    private static byte[]  message;

    private String message2="abcdefg";

    private String massage3="abcdefgabcdefg";

    private int count=0;
    static {
        message=("abcdefg"+System.getProperty("line.separator")).getBytes();
    }

    //通道准备就绪事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端上下文对象"+ctx);
        ByteBuf byteBuf=null;
        for(int i=0;i<10;i++){
//            String str="";
//            if(i%2==0){
//                str=massage3+System.getProperty("line.separator");
//            }else{
//                str=message2+"$";
//            }
//            byteBuf=Unpooled.copiedBuffer(message2.getBytes());
//            ctx.writeAndFlush(byteBuf);
        }
    }

    //读取数据事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String byteBuf= (String) msg;
        System.out.println("接受到服务端的数据:"+byteBuf);
//        System.out.println(++count);
    }



}
