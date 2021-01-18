package com.NettyStudy.demo2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutBoundAdapter  extends ChannelOutboundHandlerAdapter{

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        super.flush(ctx);
        System.out.println("flush OutBoundAdapter");
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
       super.write(ctx,msg,promise);
        System.out.println("write OutBoundAdapter");
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
       super.read(ctx);
        /**
         * 切记：
         * 1、出栈 处理器的执行，并不是一定要 在 给客户端写数据的时候，才执行出栈处理器
         *  本类是个出栈处理器。这个方法是read()方法。它是怎么触发的呢？ ：
         *    bossGroup接收到一个客户端连接后，会执行 ServerBootStrapAcceptor的 channelRead()方法
         *    ServerBootStrapAcceptor紧接着会 开启工作线程，并且执行register（），就是在这个register（）方法
         *    中，执行了 pipeline.fireChannelActive()，然后触发了pipeline出栈处理器的 read（）方法。
         *    然后呢，最终到了 head这个出栈处理器的read（）方法，进行了改变 socketChannel的感兴趣事件
         * 2、入栈，出栈处理器方法的，事件往下传播 并不一定是  ctx.fire....,有的需要执行父类方法，
         *   才能继续往下传播，就比如本方法。如果本方法不执行父类的 read（）方法，那么事件就传播不到
         *   head节点中，也就无法改变 socketChannel的感兴趣事件，也就没法读取不到客户端消息
         */
    }
}
