package com.NIOStudy.NIO1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author:chengcheng
 * @date:2020.09.29
 */
public class NioSocekt1 {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("127.0.0.1", 6378));
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        //注册channel，并指定感兴趣的事件是Accept
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        Iterator<SelectionKey> it = selector.keys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            System.out.println("start:" + key.interestOps());
        }
        ByteBuffer readBuff = ByteBuffer.allocate(1024);
        ByteBuffer writeBuff = ByteBuffer.allocate(1024);
        writeBuff.put("我收到消息啦：".getBytes());
        writeBuff.flip();
        try {
            while (true) {

                Iterator<SelectionKey> test = selector.keys().iterator();
                while (test.hasNext()) {
                    SelectionKey ff = test.next();
                    System.out.println("进入while循环，感兴趣的key是:" + ff.interestOps());
                }

                int nReady = selector.select();//该方法会阻塞，其实底层就是执行了select函数
                Set<SelectionKey> keys = selector.selectedKeys();
                // keys()是感兴趣的key。selectedkeys（）是发生了事件的key
                System.out.println("发生的事件种类的数量" + keys.size());
                Iterator<SelectionKey> ita = keys.iterator();
                while (ita.hasNext()) {
                    SelectionKey key = ita.next();
                    ita.remove();// remove后，keys集合变为空,应该是将发生事件的集合重置为空
                    System.out.println("监听到事件的key是:" + key.interestOps());
                    if (key.isAcceptable()) {
                        // 创建新的连接，并且把链接注册到selector上，而且
                        // 声明这个channel只对读操作感兴趣
                        SocketChannel socketChannel = ssc.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("有新的连接" + socketChannel.socket().getRemoteSocketAddress());
                    } else if (key.isReadable()) {
                        if (!key.isValid()||!key.isConnectable()){

                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            System.out.println("客户端"+socketChannel.socket().getRemoteSocketAddress()+ " 主动断开了连接");
                            key.interestOps(0);

                        }else {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            readBuff.clear();
                            int result = socketChannel.read(readBuff);
                            readBuff.flip();
                            System.out.println("收到消息:" + new String(readBuff.array()));

                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                    } else if (key.isWritable()) {
                        writeBuff.rewind();
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        socketChannel.write(writeBuff);
                        System.out.println("回复消息啦 回复消息啦  回复消息啦 ");

                        key.interestOps(SelectionKey.OP_READ);

                    } else {
                        System.out.println("else else else else");
                    }
                }
            }


        } catch (IOException e) {
           e.printStackTrace();
        }


    }
}
