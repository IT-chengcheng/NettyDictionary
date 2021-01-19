package com.NettyStudy.demo2;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

//客户端
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup eventExecutors=new NioEventLoopGroup();    //创建一个线程组
        //创建客户端启动助手  完成相关配置
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(eventExecutors)    //设置线程组
                 .channel(NioSocketChannel.class) //设置使用SocketChannel为管道通信的底层实现
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel socketChannel) throws Exception {
                         socketChannel.pipeline().addLast(
                                // new LengthFieldPrepender(8,false),
                                // new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4),
                                 new StringDecoder(),
                                 new StringEncoder(),
                                 new NettyClientHendler());  //将自己编写的客户端业务逻辑处理类加入到pipeline链中
                     }
                 });
        System.out.println("......client init......");
        //设置服务端的ip和端口  异步非阻塞
        ChannelFuture future = bootstrap.connect("127.0.0.1", 9090).sync();//connect方法是异步的    sync方法是同步的
       future.channel().writeAndFlush(Unpooled.copiedBuffer("我是客户端--同好", CharsetUtil.UTF_8));
        //关闭连接  异步非阻塞
        future.channel().closeFuture().sync();

      /*
           DelimiterBasedFrameDecoder extends ByteToMessageDecoder
           LineBasedFrameDecoder
           LengthFieldBasedFrameDecoder
           FixedLengthFrameDecoder
        */
    }

}
