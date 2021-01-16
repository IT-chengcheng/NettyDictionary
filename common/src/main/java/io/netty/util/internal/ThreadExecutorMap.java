/*
 * Copyright 2019 The Netty Project
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
package io.netty.util.internal;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.ThreadPerTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Allow to retrieve the {@link EventExecutor} for the calling {@link Thread}.
 */
public final class ThreadExecutorMap {

    private static final FastThreadLocal<EventExecutor> mappings = new FastThreadLocal<EventExecutor>();

    private ThreadExecutorMap() { }

    /**
     * Returns the current {@link EventExecutor} that uses the {@link Thread}, or {@code null} if none / unknown.
     */
    public static EventExecutor currentExecutor() {
        return mappings.get();
    }

    /**
     * 设置{@link Thread}使用的当前{@link EventExecutor}。
     */
    private static void setCurrentEventExecutor(EventExecutor executor) {
        mappings.set(executor);
    }

    /**
     * Decorate the given {@link Executor} and ensure {@link #currentExecutor()} will return {@code eventExecutor}
     * when called from within the {@link Runnable} during execution.
     */
    public static Executor apply(final Executor executor, final EventExecutor eventExecutor) {
        ObjectUtil.checkNotNull(executor, "executor");
        ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");

        return new Executor() {
            @Override
            public void execute(final Runnable command) {
                /**
                 * netty 的 NioEventLoop 真正创建线程，并且开启线程的方法2
                 * executor ->   ThreadPerTaskExecutor 创建一个线程，并且开启线程。
                 * eventExecutor ->  NioEventLoop extends SingleThreadEventExecutor
                 */
                executor.execute(apply(command, eventExecutor));
            }
        };
    }


    public static Runnable apply(final Runnable command, final EventExecutor eventExecutor) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
        //command= 真正执行具体任务的 线程任务对象
        //eventExecutor=NioEventLoop extends SingleThreadEventExecutor
        return new Runnable() {
            @Override
            public void run() {
                /**
                 * 类似 private ThreadLocal<String> threadLocal=new ThreadLocal<>();
                 * threadLocal.set(eventExecutor);
                 * 执行任务之前 保存一个eventExecutor -> NioEventLoop extends SingleThreadEventExecutor
                 */
                setCurrentEventExecutor(eventExecutor);
                try {
                    /**
                     * 能够进入外面的 run()方法，说明真正开启了线程
                     * 但是真正执行线程任务的还是外面传进来的 command.run()，但是这样调用，仅仅是一个方法调用，并没有再次开启线程
                     * 这样相当于一个回调，回调到外面传进来的方法块.
                     */
                    command.run();
                } finally {
                    /**
                     * 执行完，清空当前变量
                     */
                    setCurrentEventExecutor(null);
                }
            }
        };
    }

    /**
     * Decorate the given {@link ThreadFactory} and ensure {@link #currentExecutor()} will return {@code eventExecutor}
     * when called from within the {@link Runnable} during execution.
     */
    public static ThreadFactory apply(final ThreadFactory threadFactory, final EventExecutor eventExecutor) {
        ObjectUtil.checkNotNull(threadFactory, "command");
        ObjectUtil.checkNotNull(eventExecutor, "eventExecutor");
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return threadFactory.newThread(apply(r, eventExecutor));
            }
        };
    }
}
