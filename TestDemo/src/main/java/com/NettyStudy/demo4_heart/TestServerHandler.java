package com.NettyStudy.demo4_heart;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class TestServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent= (IdleStateEvent) evt;
            String str="";
            switch (idleStateEvent.state()){
                case READER_IDLE:
                    str="读空闲";
                    System.out.println(ctx.channel().remoteAddress()+"----超时事件-----"+str);
                    break;
                case WRITER_IDLE:
                    str="写空闲";
                    System.out.println(ctx.channel().remoteAddress()+"----超时事件-----"+str);
                    break;
                case ALL_IDLE:
                    str="读写空闲";
                    System.out.println(ctx.channel().remoteAddress()+"----超时事件-----"+str);
                    ctx.channel().close();
                    break;
            }
        }
    }
}
