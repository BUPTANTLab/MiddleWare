package com.ANT.MiddleWare.WiFi;

import java.io.IOException;

import android.content.Context;

public class WiFiEmpty extends WiFiPulic {
	private static final String TAG = WiFiEmpty.class.getSimpleName();

	public WiFiEmpty(Context contect) {
		super(contect);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void notify(int seg, int start) {
	}

	@Override
	public void EmergencySend(byte[] data) {
		try {
			Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//UdpSpeed.NormalUdp("192.168.1.255");
		//UdpSpeed.NormalUdp(WiFiBroad.multicastHost);
		
		//UdpSpeed.MultiUdp(WiFiBroad.multicastHost);
		
		//UdpSpeed.NioUdp("192.168.1.255");
		//UdpSpeed.NioUdp(WiFiBroad.multicastHost);
		
		//UdpSpeed.JniUdp("192.168.1.255");
		//UdpSpeed.JniUdp(WiFiBroad.multicastHost);
		
		UdpSpeed.fragment();
	}
}
