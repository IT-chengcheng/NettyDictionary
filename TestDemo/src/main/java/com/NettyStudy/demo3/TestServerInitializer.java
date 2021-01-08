package com.NettyStudy.demo3;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;


public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        /**
         * 1) lengthFieldOffset  //长度字段的偏差
         * 2) lengthFieldLength  //长度字段占的字节数
         * 3) lengthAdjustment  //添加到长度字段的补偿值
         * 4) initialBytesToStrip  //从解码帧中第一次去除的字节数
         */

        //ChannelInboundHandlerAdapter
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        //ChannelOutboundHandlerAdapter
        pipeline.addLast(new LengthFieldPrepender(4)); //计算当前待发送消息的二进制字节长度，将该长度添加到ByteBuf的缓冲区头中
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8)); //ChannelInboundHandlerAdapter
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));//ChannelOutboundHandlerAdapter
        pipeline.addLast(new TestServerHandler());//SimpleChannelInboundHandler
    }
}
