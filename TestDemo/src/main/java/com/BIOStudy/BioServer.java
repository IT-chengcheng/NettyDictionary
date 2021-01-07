package com.BIOStudy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket=new ServerSocket(9999);
            TimeServerHandlerExecutorPool timeServerHandlerExecutorPool=new TimeServerHandlerExecutorPool(50,1000);
            while (true){
                Socket socket = serverSocket.accept();  //阻塞
                System.out.println("客户端"+socket.getRemoteSocketAddress().toString()+"来连接了");
//                new Thread(new BioServerHandler(socket)).start();
                timeServerHandlerExecutorPool.execute(new BioServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
