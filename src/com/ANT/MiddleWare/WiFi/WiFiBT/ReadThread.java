package com.ANT.MiddleWare.WiFi.WiFiBT;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by ge on 2016/4/12.
 */
public class ReadThread extends Thread {
    private SocketChannel sc;

    public ReadThread(SocketChannel sc) {
        this.sc = sc;
    }

    @Override
    public void run() {
        System.out.println("read start");
        ByteBuffer buf = ByteBuffer.allocate(1024);
        try {
            while (!Thread.interrupted()) {
                int byteRead = sc.read(buf);
                if (byteRead >0) {
                    buf.flip();
                    byte[] content = new byte[buf.limit()];
                    buf.get(content);
                    ByteArrayInputStream byteArrayInputStream =
                            new ByteArrayInputStream(content);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    Message message = (Message) objectInputStream.readObject();
                    objectInputStream.close();
                    byteArrayInputStream.close();
                    System.out.println(" ’µΩmessage:"+message.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
