package com.ANT.MiddleWare.WiFi.WiFiBT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

public class PublicMethod {
    public static void writeFunc(SocketChannel sc, List<Message> msgList){
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
    public static Message[] readFunc(SocketChannel sc){
        ByteBuffer buf = ByteBuffer.allocate(1024);
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            while (!Thread.interrupted()) {
                int byteRead = sc.read(buf);
                if (byteRead >0) {
                    buf.flip();
                    byte[] content = new byte[buf.limit()];
                    System.out.println(content.length);
                    buf.get(content);
                    byteArrayInputStream =
                            new ByteArrayInputStream(content);
                    objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    return (Message[]) objectInputStream.readObject();
                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally{
        	try{
            if(objectInputStream!=null)objectInputStream.close();
            if(byteArrayInputStream!=null)byteArrayInputStream.close();
        	}catch(IOException e){
        		e.printStackTrace();
        	}
        }
		return null;
    }
}
