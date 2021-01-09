package com.NettyStudy.demo4_heart;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class TestServerLnitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //2. 配置handler
        //2.1 websocket基于http协议,需要http编码和解码工具
        pipeline.addLast(new HttpServerCodec());
        //2.2 对于大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        //2.3 对于http的消息进行聚合,聚合成FullHttpRequest或者FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(1024*64));
        //针对客户端，如果1分钟之内没有向服务器发送读写心跳，则主动断开
        pipeline.addLast(new IdleStateHandler(40,50,45));
        //自定义的读写空闲状态检测
        pipeline.addLast(new HeartBeatHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //定义自己的handler,主要是对请求进行处理和发送
        pipeline.addLast(new ChatHandler());
    }
}
