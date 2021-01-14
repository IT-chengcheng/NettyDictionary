/*
* Copyright 2014 The Netty Project
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

import io.netty.util.concurrent.EventExecutor;

final class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {

    /**
     * 各种不同类型的handler
     *    1、 ChannelInitializer  特殊的handler，程序员以及netty都可以创建这种类型的，一般创建的事内部类
     *                这个种handler的作用就是：添加真正的handler，添加完了在将自己从pipelie中移除
     *    2、 程序员自称已的handler
     *    3、netty自定义的handler
     *
     */
    private final ChannelHandler handler;

    DefaultChannelHandlerContext(
            DefaultChannelPipeline pipeline, EventExecutor executor, String name, ChannelHandler handler) {
        //pipeline=DefaultChannelPipeline
        //executor=null
        //name =没传系统默认创建
        //handler=new ChannelInitializer<Channel>
        super(pipeline, executor, name, handler.getClass());
        /**
         * handler -> 会有各种 handler，程序员自己加的(TestZhangHandler)，netty自己的,
         *  还有一种最为特殊的handler  -> ChannelInitializer
         * abstract  ChannelInitializer<Channel> 这是个特殊的handler,一般 netty 创建的 ChannelInitializer都是一个匿名内部类 ->
         *                                                  （见 -> ServerBootStrap -> init(Channel channel)）
         * 程序员创建的可以是个匿名内部类，也可以创建一个它的子类。
         */
        this.handler = handler;
    }

    @Override
    public ChannelHandler handler() {
        return handler;
    }
}
