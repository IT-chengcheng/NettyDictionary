/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * {@link Bootstrap} sub-class which allows easy bootstrap of {@link ServerChannel}
 *
 */
public class ServerBootstrap extends AbstractBootstrap<ServerBootstrap, ServerChannel> {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ServerBootstrap.class);

    private final Map<ChannelOption<?>, Object> childOptions = new LinkedHashMap<ChannelOption<?>, Object>();
    private final Map<AttributeKey<?>, Object> childAttrs = new LinkedHashMap<AttributeKey<?>, Object>();
    private final ServerBootstrapConfig config = new ServerBootstrapConfig(this);
    //workerGroup -> NioEventLoopGroup  处理读写事件的group
    private volatile EventLoopGroup childGroup;
    /**
     * 处理读写事件的handler
     * 但是一般都是 ChannelInitializer ，这是个特殊的handler，一般都是重写它的方法 initChannel(),在方法内部拿到pipeline
     * 然后再继续添加多个真正的handler，添加完成后，将ChannelInitializer从pipeline中移除
     */
    private volatile ChannelHandler childHandler;

    public ServerBootstrap() { }

    private ServerBootstrap(ServerBootstrap bootstrap) {
        super(bootstrap);
        childGroup = bootstrap.childGroup;
        childHandler = bootstrap.childHandler;
        synchronized (bootstrap.childOptions) {
            childOptions.putAll(bootstrap.childOptions);
        }
        synchronized (bootstrap.childAttrs) {
            childAttrs.putAll(bootstrap.childAttrs);
        }
    }

    /**
     * Specify the {@link EventLoopGroup} which is used for the parent (acceptor) and the child (client).
     */
    @Override
    public ServerBootstrap group(EventLoopGroup group) {
        return group(group, group);
    }

    /**
     * Set the {@link EventLoopGroup} for the parent (acceptor) and the child (client). These
     * {@link EventLoopGroup}'s are used to handle all the events and IO for {@link ServerChannel} and
     * {@link Channel}'s.
     */
    public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
        //bossGroup,workerGroup
        super.group(parentGroup);
        ObjectUtil.checkNotNull(childGroup, "childGroup");
        if (this.childGroup != null) {
            throw new IllegalStateException("childGroup set already");
        }
        //workerGroup -> NioEventLoopGroup  处理读写事件的group
        this.childGroup = childGroup;
        return this;
    }

    /**
     * Allow to specify a {@link ChannelOption} which is used for the {@link Channel} instances once they get created
     * (after the acceptor accepted the {@link Channel}). Use a value of {@code null} to remove a previous set
     * {@link ChannelOption}.
     */
    public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value) {
        //childOptions=new LinkedHashMap<ChannelOption<?>, Object>();
        ObjectUtil.checkNotNull(childOption, "childOption");
        if (value == null) {
            synchronized (childOptions) {
                childOptions.remove(childOption);
            }
        } else {
            synchronized (childOptions) {
                childOptions.put(childOption, value);
            }
        }
        return this;
    }

    /**
     * Set the specific {@link AttributeKey} with the given value on every child {@link Channel}. If the value is
     * {@code null} the {@link AttributeKey} is removed
     */
    public <T> ServerBootstrap childAttr(AttributeKey<T> childKey, T value) {
        ObjectUtil.checkNotNull(childKey, "childKey");
        if (value == null) {
            childAttrs.remove(childKey);
        } else {
            childAttrs.put(childKey, value);
        }
        return this;
    }

    /**
     * Set the {@link ChannelHandler} which is used to serve the request for the {@link Channel}'s.
     */
    public ServerBootstrap childHandler(ChannelHandler childHandler) {
        // this.childHandler=new ChannelInitializer<ServerSocketChannel>()
        this.childHandler = ObjectUtil.checkNotNull(childHandler, "childHandler");
        return this;
    }

    @Override
    void init(Channel channel) throws Exception {

        //channel=NioServerSocketChannel
        //options0  获取的是用户自己设置的tcp参数  ServerBootstrap.option(ChannelOption.SO_BACKLOG,128)
        final Map<ChannelOption<?>, Object> options = options0();
        synchronized (options) {
            //设置用户设置的tcp参数
            setChannelOptions(channel, options, logger);
        }
        //attrs0()  ServerBootstrap.attr()  获取用户设置的attr参数
        final Map<AttributeKey<?>, Object> attrs = attrs0();
        synchronized (attrs) {
            for (Entry<AttributeKey<?>, Object> e: attrs.entrySet()) {
                @SuppressWarnings("unchecked")
                AttributeKey<Object> key = (AttributeKey<Object>) e.getKey();
                channel.attr(key).set(e.getValue());
            }
        }

        //channel=NioServerSocketChannel
        // p -> DefaultChannelPipeline
        ChannelPipeline p = channel.pipeline();

        final EventLoopGroup currentChildGroup = childGroup;
        // 一般都是 ChannelInitializer ，这是个特殊的handler，见属性解释
        final ChannelHandler currentChildHandler = childHandler;
        final Entry<ChannelOption<?>, Object>[] currentChildOptions;
        final Entry<AttributeKey<?>, Object>[] currentChildAttrs;
        synchronized (childOptions) {
            currentChildOptions = childOptions.entrySet().toArray(newOptionArray(0));
        }
        synchronized (childAttrs) {
            currentChildAttrs = childAttrs.entrySet().toArray(newAttrArray(0));
        }
        // p -> DefaultChannelPipeline
        p.addLast(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(final Channel ch) throws Exception {
                // ch -> NioServerSocketChannel
                // ch.pipeline() -> DefaultChannelPipeline
                final ChannelPipeline pipeline = ch.pipeline();

                /**
                 * config.handler() 是 自己创建的，处理客户端连接的handler
                 * 如果程序员没有自己新建的 “用于处理客户端连接的” handler，那就不用加
                 * 这样一来，处理客户端连接的handler 全都是netty自己默认的
                 * 继续往下看代码，会看到 pipeline又加了一个 ServerBootstrapAcceptor  extends ChannelInboundHandlerAdapter
                 * 并且这个 特殊的handler ChannelInitializer 会从DefaultChannelPipeline移除掉，最终 pipeline里面入出现如下链状结构
                 *   head -> ServerBootstrapAcceptor -> tail  (前提是程序员没有自己添加“用于处理客户端连接的” handler)
                 */
                ChannelHandler handler = config.handler();
                if (handler != null) {
                    pipeline.addLast(handler);
                }
                // ch.eventLoop() -> NioEventLoop
                ch.eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 继续添加用于处理客户端连接的 handler
                         *  bossGroup将客户端连接转交给workerGroup
                         *  ServerBootstrapAcceptor  extends ChannelInboundHandlerAdapter
                         *  currentChildHandler 一般都是 ChannelInitializer ，这是个特殊的handler，这个特殊的handler
                         *  1、跟外面的这个 ChannelInitializer 相同点：
                         *  都是一个特殊的handler，用完接着 从DefaultChannelPipeline中移除，它的作用就是添加真正的handler
                         *  2、currentChildHandler 也就是 childHandler 跟外面的这个 ChannelInitializer 不同点：
                         *       外面的ChannelInitializer 添加的handler 是处理客户端连接的
                         *       currentChildHandler 添加的handler 是 处理读写事件的，看下 new ServerBootstrapAcceptor()入参
                         *        就包含这个 currentChildHandler （childHandler ），说明 ServerBootstrapAcceptor 内部就是拿到
                         *        客户端连接后，又开启pipeline，然后执行匿名内部类 currentChildHandler ，添加各种处理读写事件的handler
                         *          执行的是程序员 加的 bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {}，
                         *          这个内名内部类的  public void initChannel(final Channel ch).
                         */
                        pipeline.addLast(new ServerBootstrapAcceptor(
                                ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
                    }
                });
            }
        });
    }

    @Override
    public ServerBootstrap validate() {
        super.validate();
        // 父类判断处理连接的属性，子类判断处理读写事件的属性
        if (childHandler == null) {
            throw new IllegalStateException("childHandler not set");
        }
        if (childGroup == null) {
            logger.warn("childGroup is not set. Using parentGroup instead.");
            childGroup = config.group();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private static Entry<AttributeKey<?>, Object>[] newAttrArray(int size) {
        return new Entry[size];
    }

    @SuppressWarnings("unchecked")
    private static Map.Entry<ChannelOption<?>, Object>[] newOptionArray(int size) {
        return new Map.Entry[size];
    }
    // 拿到连接成功的 socket，开启 workGroup
    private static class ServerBootstrapAcceptor extends ChannelInboundHandlerAdapter {

        private final EventLoopGroup childGroup;// 见构造方法解释
        private final ChannelHandler childHandler;// 见构造方法解释
        private final Entry<ChannelOption<?>, Object>[] childOptions;
        private final Entry<AttributeKey<?>, Object>[] childAttrs;
        private final Runnable enableAutoReadTask;

        ServerBootstrapAcceptor(
                final Channel channel, EventLoopGroup childGroup, ChannelHandler childHandler,
                Entry<ChannelOption<?>, Object>[] childOptions, Entry<AttributeKey<?>, Object>[] childAttrs) {

            // channel -> NioServerSocketChannel

            /**
             * childGroup 处理客户端连接的 线程组,里面有个属性，就是线程组数组 ：
             *       EventExecutor[] children -> 存放 nThreads 个 NioEventLoop 实例。
             *        bossGroup ：nThreads = 1；
             *        workGrop  ：nThreads = 16
             * 这里肯定是 workGrop ！！！，也就是有16个NioEventLoop，实际数字= CPU核数 * 2
             */
            this.childGroup = childGroup;
            //自定义的ChannelInitializer，这是个特殊的handler，专门用来添加真正的handler，添加完后，将此特殊hanlder从pipeline中移除
            this.childHandler = childHandler;
            this.childOptions = childOptions;
            this.childAttrs = childAttrs;

            // Task which is scheduled to re-enable auto-read.
            // It's important to create this Runnable before we try to submit it as otherwise the URLClassLoader may
            // not be able to load the class because of the file limit it already reached.
            //
            // See https://github.com/netty/netty/issues/1328
            enableAutoReadTask = new Runnable() {
                @Override
                public void run() {
                    // channel.config() -> NioServerSocketChannelConfig
                    channel.config().setAutoRead(true);
                }
            };
        }

        @Override
        @SuppressWarnings("unchecked")
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            /**
             * 这个方法就是 开启 workGroup！！！！ 注册读写事件，处理读写事件！！！
             */
            final Channel child = (Channel) msg;
            // 此时的 msg 一定是 NioSocketChannel
            child.pipeline().addLast(childHandler);

            setChannelOptions(child, childOptions, logger);

            for (Entry<AttributeKey<?>, Object> e: childAttrs) {
                child.attr((AttributeKey<Object>) e.getKey()).set(e.getValue());
            }

            try {
                childGroup.register(child).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            forceClose(child, future.cause());
                        }
                    }
                });
            } catch (Throwable t) {
                forceClose(child, t);
            }
        }

        private static void forceClose(Channel child, Throwable t) {
            child.unsafe().closeForcibly();
            logger.warn("Failed to register an accepted channel: {}", child, t);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final ChannelConfig config = ctx.channel().config();
            if (config.isAutoRead()) {
                // stop accept new connections for 1 second to allow the channel to recover
                // See https://github.com/netty/netty/issues/1328
                config.setAutoRead(false);
                ctx.channel().eventLoop().schedule(enableAutoReadTask, 1, TimeUnit.SECONDS);
            }
            // still let the exceptionCaught event flow through the pipeline to give the user
            // a chance to do something with it
            ctx.fireExceptionCaught(cause);
        }
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public ServerBootstrap clone() {
        return new ServerBootstrap(this);
    }

    /**
     * Return the configured {@link EventLoopGroup} which will be used for the child channels or {@code null}
     * if non is configured yet.
     *
     * @deprecated Use {@link #config()} instead.
     */
    @Deprecated
    public EventLoopGroup childGroup() {
        return childGroup;
    }

    final ChannelHandler childHandler() {
        return childHandler;
    }

    final Map<ChannelOption<?>, Object> childOptions() {
        return copiedMap(childOptions);
    }

    final Map<AttributeKey<?>, Object> childAttrs() {
        return copiedMap(childAttrs);
    }

    @Override
    public final ServerBootstrapConfig config() {
        return config;
    }
}
