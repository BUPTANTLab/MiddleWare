package com.ANT.MiddleWare.WiFi.WiFiBT;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.ANT.MiddleWare.PartyPlayerActivity.MainActivity;
import com.ANT.MiddleWare.WiFi.WiFiBT.Message.Type;

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
    	Message[]message =PublicMethod.readFunc(sc);
    	System.out.println("receive size :"+String.valueOf(message.length));
        handleMessage(message[1]);
        response(sc);

    }
    
    private void response(SocketChannel sc){
    	PeerMessage msg = new PeerMessage();
    	msg.setMessage("hello I am client 2");
    	msg.setType(Type.PEER);
    	
    	
    }
    private void handleMessage(Message message){
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
    }
}
