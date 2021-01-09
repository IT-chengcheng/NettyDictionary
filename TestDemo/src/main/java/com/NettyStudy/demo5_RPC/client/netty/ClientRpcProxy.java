package com.NettyStudy.demo5_RPC.client.netty;

import com.NettyStudy.demo5_RPC.entity.ClassInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ClientRpcProxy {

    public static Object create(Class clazz){
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                ClassInfo classInfo=new ClassInfo();
                classInfo.setClassName(clazz.getName());
                classInfo.setMethodName(method.getName());
                classInfo.setArgs(args);
                classInfo.setClazzType(method.getParameterTypes());

                EventLoopGroup eventExecutors=new NioEventLoopGroup();    //创建一个线程组
                //创建客户端启动助手  完成相关配置
                Bootstrap bootstrap=new Bootstrap();
                //创建业务处理类
                ClientSocketNettyHendler nettyClientHendler = new ClientSocketNettyHendler();
                try {
                    bootstrap.group(eventExecutors)    //设置线程组
                            .channel(NioSocketChannel.class) //设置使用SocketChannel为管道通信的底层实现
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    //添加编码器
                                    pipeline.addLast("encoder",new ObjectEncoder());

                                    //添加解码器
                                    //maxObjectSize:序列化的对象的最大长度，一旦接收到的对象长度大于此值，抛出StreamCorruptedException异常
                                    //classResolver：这个类（ClassResolver）会去加载已序列化的对象，
                                    //常用调用方式：ClassResolvers.cacheDisabled(Plan.class.getClassLoader())
                                    //或者直接ClassResolvers.cacheDisabled(null)
                                    pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));//
                                    //将自己编写的客户端业务逻辑处理类加入到pipeline链中
                                    pipeline.addLast(nettyClientHendler);
                                }
                            });
                    System.out.println("......client init......");
                    //设置服务端的ip和端口  异步非阻塞
                    ChannelFuture future = bootstrap.connect("127.0.0.1", 9090).sync();  //connect方法是异步的    sync方法是同步的
                    future.channel().writeAndFlush(classInfo).sync();
                    //关闭连接  异步非阻塞
                    future.channel().closeFuture().sync();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return nettyClientHendler.getResponse();
            }
        });
    }

}
