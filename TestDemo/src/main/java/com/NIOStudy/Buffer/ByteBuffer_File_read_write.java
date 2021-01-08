package com.NIOStudy.Buffer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class ByteBuffer_File_read_write {
    public static void main(String[] args) throws Exception {
        FileOutputStream fileOutputStream=new FileOutputStream("dome4write.txt");
        FileInputStream fileInputStream=new FileInputStream("dome4read.txt");

        FileChannel channelRead = fileInputStream.getChannel();
        FileChannel channelWrite = fileOutputStream.getChannel();
        ByteBuffer byteBuffer=ByteBuffer.allocate(5);
        StringBuilder b = new StringBuilder();
       while (true){
           /**
            * 之所以每次都要clear一下，我猜测是因为要想往buffer添加数据，必须要position=0，不然添加不进去
            */
           byteBuffer.clear();
           System.out.println("position:"+byteBuffer.position() + "  limit:"+byteBuffer.limit());
           int readNumber = channelRead.read(byteBuffer);
           // 这个readNumber就是读取的字节大小，字母，数字占一个字节，汉字占三个字节
           System.out.println("readNumber:"+readNumber);
           if(-1==readNumber){
               break;
           }
           System.out.println("after-read position:"+byteBuffer.position());
           byteBuffer.flip();

        /*   String temp111 = new String(byteBuffer.array());
           byteBuffer.toString();*/
           String temp = Charset.forName("UTF-8").decode(byteBuffer).toString();
           System.out.println("读取:"+temp);


           b.append(temp);
           channelWrite.write(byteBuffer);

       }
       fileOutputStream.close();
       fileInputStream.close();
       System.out.println("结束："+b.toString());
    }
}
