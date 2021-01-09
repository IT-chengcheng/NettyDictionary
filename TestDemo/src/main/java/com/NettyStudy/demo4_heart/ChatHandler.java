package com.NettyStudy.demo4_heart;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 自定义handler,继承简单频道入站处理程序,范围为wen文本套接字Frame
 * websocket间通过frame进行数据的传递和发送
 * 此版本为user与channel绑定的版本，消息会定向发送和接收到指定的user的channel中。
 *
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    
    //定义channel集合,管理channel,传入全局事件执行器
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 定义信道的消息处理机制,该方法处理一次,故需要同时对所有客户端进行操作(channelGroup)
     * @param ctx 上下文
     * @param msg 文本消息
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //1. 获取客户端传递过来的消息,其对象为TextWebSocketFrame
        String text = msg.text();
        System.out.println("接收到数据为: "+ text);
    }

    /**
     * 当客户端连接服务端之后(打开连接)----->handlerAdded
     * 获取客户端的channel,并且放到ChannelGroup中去管理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
        String asShortText = ctx.channel().id().asShortText();
        System.out.println("客户端添加，channelId为：" + asShortText);
    }

    //处理器移除时,移除channelGroup中的channel
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //打印移除的channel
        String asShortText = ctx.channel().id().asShortText();
        System.out.println("客户端被移除，channelId为：" + asShortText);
        users.remove(ctx.channel());
    }

    /**
     * 发生异常时，关闭连接（channel），随后将channel从ChannelGroup中移除
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("出错啦, 原因是:"+cause.getMessage());
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
