package com.ANT.MiddleWare.WiFi;

import java.io.IOException;

import com.ANT.MiddleWare.WiFi.WiFiBroad.WiFiBroad;

import android.content.Context;
import android.util.Log;

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
		Log.e(TAG, "NormalUdp");
		UdpSpeed.NormalUdp("192.168.1.255");
		Log.e(TAG, "NormalUdp");
		UdpSpeed.NormalUdp(WiFiBroad.multicastHost);
		Log.e(TAG, "MultiUdp");
		UdpSpeed.MultiUdp(WiFiBroad.multicastHost);
		Log.e(TAG, "NioUdp");
		UdpSpeed.NioUdp("192.168.1.255");
		Log.e(TAG, "NioUdp");
		UdpSpeed.NioUdp(WiFiBroad.multicastHost);
		Log.e(TAG, "JniUdp");
		UdpSpeed.JniUdp();
	}
}
