package com.NettyStudy.demo5_RPC.server.netty;

import com.NettyStudy.demo5_RPC.entity.ClassInfo;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ChannelHandler.Shareable
public class ServerSocketNettyHendler extends ChannelInboundHandlerAdapter {

    public static ServerSocketNettyHendler serverSocketNettyHendler=new ServerSocketNettyHendler();


    private static ExecutorService executorService= Executors.newFixedThreadPool(1000);

    //得到某个接口下的实现类
    public String getImplClassName(ClassInfo classInfo) throws Exception {
        //服务器接口与实现类地址;
        String iName="com.luban.netty.lbrpc.server.service";
        int i = classInfo.getClassName().lastIndexOf(".");
        String className=classInfo.getClassName().substring(i);
        Class aClass = Class.forName(iName + className);
        Reflections reflections=new Reflections(iName);
        Set<Class<?>> classes=reflections.getSubTypesOf(aClass);
        if(classes.size()==0){
            System.out.println("未找到实现类");
            return null;
        }else if(classes.size()>1){
            System.out.println("找到多个实现类，未明确使用哪个实现类");
            return null;
        }else{
            Class[] classes1 = classes.toArray(new Class[0]);
            return classes1[0].getName();
        }
    }


    //读取数据事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ClassInfo classInfo= (ClassInfo) msg;
                    Object o = Class.forName(getImplClassName(classInfo)).newInstance();
                    Method method = o.getClass().getMethod(classInfo.getMethodName(), classInfo.getClazzType());
                    Object invoke = method.invoke(o, classInfo.getArgs());
                    ctx.channel().writeAndFlush(invoke);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
