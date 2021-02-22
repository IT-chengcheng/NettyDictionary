package com.BIOStudy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BioServerHandler implements Runnable {
    //负责客户端通信
    private Socket socket;

    public BioServerHandler(Socket socket){
        this.socket=socket;
    }

    @Override
    public void run() {
        InputStream inputStream=null;
        OutputStream outputStream=null;
        try {
            inputStream = socket.getInputStream();
            outputStream=socket.getOutputStream();
            int count=0;
            String content=null;
            byte[] bytes=new byte[1000];
            while ((count=inputStream.read(bytes))!=-1){
                String line=new String(bytes,0,count,"utf-8");
                System.out.println("服务端收到消息:"+line);
                outputStream.write("收到你发的消息啦，你最近好吗？".getBytes("utf-8"));
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
