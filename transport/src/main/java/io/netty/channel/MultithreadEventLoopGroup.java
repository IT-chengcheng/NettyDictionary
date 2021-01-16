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
package io.netty.channel;

import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Abstract base class for {@link EventLoopGroup} implementations that handles their tasks with multiple threads at
 * the same time.
 */
public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MultithreadEventLoopGroup.class);

    private static final int DEFAULT_EVENT_LOOP_THREADS;

    static {
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));

        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.eventLoopThreads: {}", DEFAULT_EVENT_LOOP_THREADS);
        }
    }

    /**
     * @see MultithreadEventExecutorGroup#MultithreadEventExecutorGroup(int, Executor, Object...)
     * nThreads如果不传默认是0  如果是0的话  就获取CPU核数的两倍
     */
    protected MultithreadEventLoopGroup(int nThreads, Executor executor, Object... args) {
        /**
         * nThreads默认为零
         * executor默认为null
         * nThreads如果不传默认是0  如果是0的话  就获取CPU核数的两倍  DEFAULT_EVENT_LOOP_THREADS==CPU核数的两倍
         */
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, args);
    }

    /**
     * @see MultithreadEventExecutorGroup#MultithreadEventExecutorGroup(int, ThreadFactory, Object...)
     */
    protected MultithreadEventLoopGroup(int nThreads, ThreadFactory threadFactory, Object... args) {
        //nThreads默认不传就是0
        //threadFactory默认为null
        //ServerSocketChannel    就是通过ServerSocketChannel.open()==》SelectorProvider.provider().openServerSocketChannel()创建的
        //DefaultSelectStrategyFactory.INSTANCE===》new DefaultSelectStrategyFactory()
        //RejectedExecutionHandlers.reject() ===》 new RejectedExecutionHandler()
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, threadFactory, args);
    }

    /**
     * @see MultithreadEventExecutorGroup#MultithreadEventExecutorGroup(int, Executor,
     * EventExecutorChooserFactory, Object...)
     */
    protected MultithreadEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory,
                                     Object... args) {
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, chooserFactory, args);
    }

    @Override
    protected ThreadFactory newDefaultThreadFactory() {
        //Thread.MAX_PRIORITY   ==>  线程可以拥有的最大优先级。  10
        //getClass()===>NioEventLoopGroup.class
        return new DefaultThreadFactory(getClass(), Thread.MAX_PRIORITY);
    }

    @Override
    public EventLoop next() {
        /**
         * 从NioEventLoopGroup线程组executors中 根据一定算法 返回一个线程执行对象 NioEventLoop
         * bossGroup 一共 一个线程
         * workGroup 一共 CPU核数 * 2 个线程
         */
        return (EventLoop) super.next();
    }

    @Override
    protected abstract EventLoop newChild(Executor executor, Object... args) throws Exception;

    @Override
    public ChannelFuture register(Channel channel) {
        /**
         * channel= NioServerSocketChannel 或者 NioSocketChannel
         * next()= NioEventLoop
         * next()是 :
         * 从NioEventLoopGroup线程组executors中 根据一定算法 返回一个线程执行对象 NioEventLoop
         * bossGroup 一共 一个线程
         * workGroup 一共 CPU核数 * 2 个线程
         */
        return next().register(channel);
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        return next().register(promise);
    }

    @Deprecated
    @Override
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        return next().register(channel, promise);
    }
}
