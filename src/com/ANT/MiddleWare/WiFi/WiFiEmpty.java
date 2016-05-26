package com.ANT.MiddleWare.WiFi;

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
		Log.e(TAG, "NormalUdp");
		//UdpSpeed.NormalUdp();
		Log.e(TAG, "NioUdp");
		//UdpSpeed.NioUdp();
		Log.e(TAG, "JniUdp");
		UdpSpeed.JniUdp();
	}
}
