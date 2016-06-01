package com.ANT.MiddleWare.WiFi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ANT.MiddleWare.WiFi.WiFiBroad.ObjectMulti;
import com.ANT.MiddleWare.WiFi.WiFiBroad.RecvMulti;
import com.ANT.MiddleWare.WiFi.WiFiBroad.SendMulti;
import com.ANT.MiddleWare.WiFi.WiFiBroad.WiFiBroad;

public class WiFiEmpty extends WiFiPulic {
	private static final String TAG = WiFiEmpty.class.getSimpleName();
	private Process proc;
	private WifiManager wifi;
	private TelephonyManager tm;
	public static String myIP;
	public static final String baseIP = "192.168.1.";
	private int numIP;

	public WiFiEmpty(Context contect) {
		super(contect);
	    try {
	    	tm = (TelephonyManager) contect
					.getSystemService(Activity.TELEPHONY_SERVICE);
			String s = tm.getDeviceId();
			int len = s.length();
			numIP = Integer.parseInt(s.substring(len - 2));
			this.myIP = baseIP + numIP;
			Log.v(TAG, "ip " + myIP);
			proc = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(proc.getOutputStream());
			os.writeBytes("ifconfig wlan0 up\n");
			os.writeBytes("wpa_supplicant -iwlan0 -c/data/misc/wifi/wpa_supplicant.conf -B\n");
			os.writeBytes("ifconfig wlan0 " + myIP + " netmask 255.255.255.0\n");
			os.writeBytes("ip route add 224.0.0.0/4 dev wlan0\n");
			os.writeBytes("dmesg >/data/misc/wifi/2dmesgaft.txt\n");
			os.writeBytes("exit\n");
			os.flush();
			proc.waitFor();

			wifi = (WifiManager) contect.getSystemService(Context.WIFI_SERVICE);
			if (wifi != null) {
				WifiManager.MulticastLock lock = wifi
						.createMulticastLock("Log_Tag");
				lock.acquire();
			}
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }
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
		
		UdpSpeed.NormalUdp("192.168.1.255");
		UdpSpeed.NormalUdp(WiFiBroad.multicastHost);
		
		UdpSpeed.MultiUdp(WiFiBroad.multicastHost);
		
		UdpSpeed.NioUdp("192.168.1.255");
		UdpSpeed.NioUdp(WiFiBroad.multicastHost);
		
		UdpSpeed.JniUdp("192.168.1.255");
		UdpSpeed.JniUdp(WiFiBroad.multicastHost);
		
		UdpSpeed.fragment();
	}
}
