package com.ANT.MiddleWare.WiFi.WiFiBT;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * Created by ge on 2016/4/12.
 */
public class SendImpl implements Runnable {
    private InetAddress remoteAddress;
    private int remotePort;
    private ExecutorService es;
    public SendImpl(InetAddress remoteAddress,int remotePort,ExecutorService es) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.es = es;
    }

    @Override
    public void run() {
        SocketChannel sc=null;
        try {
                sc = SocketChannel.open();
            sc.connect(new InetSocketAddress(remoteAddress.getHostAddress(), remotePort));
            System.out.println("try to connect");
                if (sc.isConnected()) {
                    System.out.println("connection start");
                    WriteThread wt = new WriteThread(sc);
                    es.execute(wt);
                    try {
                        wt.join();
                        System.out.println("ready to send");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("connect fail");
        }finally {
            if (sc!=null&&(sc.isConnected())) {

//                    sc.close();
                    System.out.println("sc connected");

            }
            Thread.yield();
        }
    }
}
