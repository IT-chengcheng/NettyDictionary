package com.NIOStudy.NIO_Buffer;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteBuffer_File_read {
    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream=new FileInputStream("dome2.txt");
        FileChannel channel = fileInputStream.getChannel();
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        channel.read(byteBuffer);
        byteBuffer.flip();
        while (byteBuffer.remaining()>0){
            System.out.println((char)byteBuffer.get());
        }
        fileInputStream.close();
    }
}
