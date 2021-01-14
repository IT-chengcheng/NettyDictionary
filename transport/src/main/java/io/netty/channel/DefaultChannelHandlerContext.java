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

    //handler=new ChannelInitializer<Channel>
    private final ChannelHandler handler;

    DefaultChannelHandlerContext(
            DefaultChannelPipeline pipeline, EventExecutor executor, String name, ChannelHandler handler) {
        //pipeline=DefaultChannelPipeline
        //executor=null
        //name =没传系统默认创建
        //handler=new ChannelInitializer<Channel>
        super(pipeline, executor, name, handler.getClass());
        /**
         * handler  -> 一般都是 ChannelInitializer ，这是个特殊的handler
         *           一般ChannelInitializer都是一个匿名内部类，
         *                有程序员创建的，
         *                也有netty自己创建的（见 -> ServerBootStrap -> init(Channel channel)）
         */
        this.handler = handler;
    }

    @Override
    public ChannelHandler handler() {
        return handler;
    }
}
