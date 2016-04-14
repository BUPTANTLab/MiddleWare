package com.ANT.MiddleWare.WiFi.WiFiBT;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.ANT.MiddleWare.PartyPlayerActivity.MainActivity;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ge on 2016/4/12.
 */
public class ReadThread extends Thread {
    private SocketChannel sc;
    private Context context;
	private String result;
    public ReadThread(SocketChannel sc,Context context) {
        this.sc = sc;
        this.context = context;
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
                    result = " ’µΩmessage:"+message.getMessage()+" type:"+message.getType().toString();
                    System.out.println(result);
                    MainActivity activity = (MainActivity) context;
                    activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
						}
					});
//                    Toast.makeText(context, message.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
