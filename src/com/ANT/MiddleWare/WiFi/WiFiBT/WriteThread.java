package com.ANT.MiddleWare.WiFi.WiFiBT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by ge on 2016/4/12.
 */
public class WriteThread extends Thread {
    private SocketChannel sc;
    public WriteThread(SocketChannel sc) {
        this.sc = sc;
    }
    @Override
    public void run() {
        System.out.println("write start");
        Message msgObj = new Message();
        byte[] bytesObj = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(msgObj);
            bytesObj = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buf = ByteBuffer.allocate(bytesObj.length);
        buf.put(bytesObj);
        buf.flip();
        while (buf.hasRemaining()) {
            try {
                sc.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("write finish");
    }
}