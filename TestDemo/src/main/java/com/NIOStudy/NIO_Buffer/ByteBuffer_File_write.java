package com.NIOStudy.NIO_Buffer;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteBuffer_File_write {
    public static void main(String[] args) throws Exception {
        FileOutputStream fileOutputStream=new FileOutputStream("dome3.txt");
        FileChannel channel = fileOutputStream.getChannel();
        byte[] bytes="taibai".getBytes();
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        channel.write(byteBuffer);
        fileOutputStream.close();
    }
}
