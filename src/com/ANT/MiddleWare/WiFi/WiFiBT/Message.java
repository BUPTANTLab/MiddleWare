package com.ANT.MiddleWare.WiFi.WiFiBT;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by ge on 2016/4/12.
 */
public abstract class Message implements Serializable {
    private String fragment = "123123";
    public enum Type {A,B};
    private Type type;
    public abstract String getMessage();
    public abstract void setMessage(String msg);
    public Type getType(){
    	return type;
    }
    public void setType(Type type){
    	this.type = type;
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