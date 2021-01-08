package com.NIOStudy.Buffer;

import java.nio.ByteBuffer;

public class ByteBuffer_wrap {
    public static void main(String[] args) throws Exception {
        byte[] bytes=new byte[]{'a','b','c'};
        // 一般 服务端或者客户端 写数据的时候，会直接用这个 wrap方法
        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
        bytes[0]='b';
        byteBuffer.put(2,(byte)'b');
        for(int i=0;i<byteBuffer.capacity();i++){
            System.out.println((char)byteBuffer.get());
        }
    }
}
