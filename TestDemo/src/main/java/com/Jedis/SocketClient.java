package com.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClient {

    private Socket socket;
    public InputStream inputStream;
    public OutputStream outputStream;

    public SocketClient(String ip, int port) {
        try {
            socket=new Socket(ip,port);
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //str 向redis服务器端发送的数据
    public void send(String str){
        try {
            outputStream.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read(){
        byte[] bytes=new byte[1024];
        int count=0;
        try {
            count = inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(bytes,0,count);
    }


}
