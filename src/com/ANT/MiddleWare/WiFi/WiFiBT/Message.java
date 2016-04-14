package com.ANT.MiddleWare.WiFi.WiFiBT;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by ge on 2016/4/12.
 */
public class Message implements Serializable {
    private String fragment = "123123";
    public String getFragment() {
        return fragment;
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