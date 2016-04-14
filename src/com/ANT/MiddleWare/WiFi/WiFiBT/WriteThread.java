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
 
        	PublicMethod.writeFunc(sc, msgList);
    	
    }

}