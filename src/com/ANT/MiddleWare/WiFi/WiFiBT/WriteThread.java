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
    private Message msgObj;
    public WriteThread(SocketChannel sc,Message msgObj) {
        this.sc = sc;
        this.msgObj = msgObj;
    }
    @Override
    public void run() {
    	writeFunc(sc, msgObj);
    }
    private void writeFunc(SocketChannel sc,Message msgObj){
        System.out.println("write start");
        
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