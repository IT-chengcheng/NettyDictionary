/*
 * Copyright 2017 The Netty Project
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
package io.netty.channel.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

/**
 * 这个类是 netty真正用到 selector类，是netty自定的一个selector
 */
final class SelectedSelectionKeySetSelector extends Selector {
    /**
     * 自定义集合 SelectedSelectionKeySet extends AbstractSet<SelectionKey>
     *        自定义集合里有重要属性  SelectionKey[] keys ，存放发生事件的selectionkey
     *        并且它还重写迭代器方法，用来迭代发生事件的 SelectionKey[] keys
     */
    private final SelectedSelectionKeySet selectionKeys;
    /**
     * unwrappedSelector 类中selectedKeys替换过数据类型的Selector，
     * Set<SelectionKey> selectedKeys  ->  netty自定义的SelectedSelectionKeySet
     */
    private final Selector delegate;

    SelectedSelectionKeySetSelector(Selector delegate, SelectedSelectionKeySet selectionKeys) {
        this.delegate = delegate;
        this.selectionKeys = selectionKeys;
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public SelectorProvider provider() {
        return delegate.provider();
    }

    @Override
    public Set<SelectionKey> keys() {
        return delegate.keys();
    }

    @Override
    public Set<SelectionKey> selectedKeys() {
        return delegate.selectedKeys();
    }

    @Override
    public int selectNow() throws IOException {
        selectionKeys.reset();
        return delegate.selectNow();
    }

    @Override
    public int select(long timeout) throws IOException {
        selectionKeys.reset();
        return delegate.select(timeout);
    }

    @Override
    public int select() throws IOException {
        selectionKeys.reset();
        return delegate.select();
    }

    @Override
    public Selector wakeup() {
        return delegate.wakeup();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
