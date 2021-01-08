package com.NettyStudy.demo3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TestClientHandler extends SimpleChannelInboundHandler<String> {

    //读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(channelHandlerContext.channel().remoteAddress()+",client output :"+s);
//        channelHandlerContext.writeAndFlush("form client"+ LocalDateTime.now());
    }


    //通道就绪
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i=0;i<10;i++){
            ctx.writeAndFlush("来自客户端的问候");
        }
    }

    //有异常发生
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
