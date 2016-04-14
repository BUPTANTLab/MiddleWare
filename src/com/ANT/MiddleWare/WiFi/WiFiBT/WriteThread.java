package com.ANT.MiddleWare.WiFi.WiFiBT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ge on 2016/4/12.
 */
public class WriteThread extends Thread {
    private SocketChannel sc;
    private List<Message> msgList = new ArrayList<Message>();
    public WriteThread(SocketChannel sc,List<Message> msgList) {
        this.sc = sc;
        this.msgList = msgList;
    }
    
	@Override
    public void run() {
 
        	writeFunc(sc, msgList);
    	
    }
    private void writeFunc(SocketChannel sc, List<Message> msgList){
        System.out.println("write start");
        System.out.println("list size:"+String.valueOf(msgList.size()));
        byte[] bytesObj = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
     
        } catch (IOException e) {
            e.printStackTrace();
        }

        	try {
        		System.out.println("write one");
        		int size = msgList.size();
        		Message[] msgArr = new Message[size];
        		for(int i =0;i<size;i++){
        			msgArr[i]=msgList.get(i);
        		}
				objectOutputStream.writeObject(msgArr);
	        	bytesObj = byteArrayOutputStream.toByteArray();
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
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    
        System.out.println("write finish");
    }
}