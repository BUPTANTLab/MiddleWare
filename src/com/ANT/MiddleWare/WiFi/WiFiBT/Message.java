package com.ANT.MiddleWare.WiFi.WiFiBT;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by ge on 2016/4/12.
 */
public  abstract class Message implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1331979125147452819L;
    public static enum Type {PEER("peer message"),FRAGMENT("fragment message")
    	,NONE("not set yet");
    	private String describe;
    	private Type(String describe) {
			// TODO Auto-generated constructor stub
    		this.describe = describe;
		}
    	@Override
    	public String toString(){
    	 return describe;
    }
    
    };
    private Type type = Type.NONE;
    public abstract String getMessage();
    public abstract void setMessage(String msg);
    public Type getType(){
    	return type;
    }
    public void setType(Type type){
    	this. type = type;
    }
    
    public void getBytes() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("Message.txt"));
            outputStream.writeObject(this);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}