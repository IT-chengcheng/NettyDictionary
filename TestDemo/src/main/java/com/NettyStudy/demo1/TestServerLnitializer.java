package com.NettyStudy.demo1;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * HttpRequestEncoder，将HttpRequest或HttpContent编码成ByteBuf
   HttpRequestDecoder，将ByteBuf解码成HttpRequest和HttpContent
   HttpResponseEncoder，将HttpResponse或HttpContent编码成ByteBuf
   HttpResponseDecoder，将ByteBuf解码成HttpResponse和HttpContent
 */
//ChannelInitializer   特殊的Handler
public class TestServerLnitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("httpServerCodec",new HttpServerCodec());
        pipeline.addLast("testServerHandler",new TestServerHandler());
    }
}
