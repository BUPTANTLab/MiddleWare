package com.ANT.MiddleWare.jni;

public class udpSend {
	public native void init(int port);

	public native int send(byte[] b);
}
