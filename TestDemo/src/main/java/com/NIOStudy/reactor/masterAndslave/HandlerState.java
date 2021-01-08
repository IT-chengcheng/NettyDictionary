package com.NIOStudy.reactor.masterAndslave;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public interface HandlerState {

    void changeState(TCPHandler h);

    void handle(TCPHandler h, SelectionKey sk, SocketChannel sc,
                ThreadPoolExecutor pool) throws IOException ;
}