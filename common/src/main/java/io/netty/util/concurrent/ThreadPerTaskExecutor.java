/*
 * Copyright 2013 The Netty Project
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
package io.netty.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Executor是一个顶层接口，在它里面只声明了一个方法execute(Runnable)，返回值为void，
 * 参数为Runnable类型，从字面意思可以理解，就是用来执行传进去的任务的；
 */
public final class ThreadPerTaskExecutor implements Executor {
    //DefaultThreadFactory
    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }
        this.threadFactory = threadFactory;
    }

    //我们要执行开启一个线程时，只需要调用此方法传一个Runnable任务  此方法就会通过threadFactory创建一个线程去执行
    @Override
    public void execute(Runnable command) {
        //new DefaultThreadFactory()
        threadFactory.newThread(command).start();
    }
}
