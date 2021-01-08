package com.NIOStudy.reactor.masterAndslave;
      
    import java.io.IOException;

public class Main {


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            TCPReactor reactor = new TCPReactor(1333);
//                reactor.run();
            Thread thread = new Thread(reactor);
            thread.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}