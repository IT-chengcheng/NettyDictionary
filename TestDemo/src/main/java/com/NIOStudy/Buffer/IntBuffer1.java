package com.NIOStudy.Buffer;

import java.nio.IntBuffer;
import java.security.SecureRandom;

public class IntBuffer1 {
    public static void main(String[] args) {
        java.nio.IntBuffer buffer = java.nio.IntBuffer.allocate(8);
        for (int i=0;i<buffer.capacity();i++){
            int nextInt = new SecureRandom().nextInt(20);
            buffer.put(nextInt);
        }
        buffer.flip();
        while (buffer.hasRemaining()){
            System.out.println(buffer.get());
        }
    }
}
