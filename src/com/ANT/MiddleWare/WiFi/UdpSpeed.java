package com.ANT.MiddleWare.WiFi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.ANT.MiddleWare.jni.udpSend;

import android.util.Log;

public class UdpSpeed {
	private static final String TAG = UdpSpeed.class.getSimpleName();
	private static final String host = "192.168.1.255";
	private static final int port = 9999;
	private static final int d = 100;
	private static final int l = 65500;

	public static void NormalUdp() {
		DatagramSocket dataSocket = null;
		try {
			dataSocket = new DatagramSocket(port);
			byte[] sendDataByte = new byte[l];
			DatagramPacket dataPacket;
			dataPacket = new DatagramPacket(sendDataByte, sendDataByte.length,
					InetAddress.getByName(host), port);
			Log.e(TAG, "time start " + dataSocket.getSendBufferSize());
			long time = System.currentTimeMillis();
			for (int i = 0; i < d; i++) {
				dataSocket.send(dataPacket);
			}
			time = System.currentTimeMillis() - time;
			Log.e(TAG, "" + (d * l * 8000.0 / 1024 / time) + " kbps");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dataSocket.close();
		}
	}

	public static void NioUdp() {
		DatagramChannel dc = null;
		try {
			dc = DatagramChannel.open();
			dc.configureBlocking(true);
			dc.socket().setBroadcast(true);
			SocketAddress address = new InetSocketAddress(host, port);
			dc.connect(address);
			byte[] b = new byte[l];
			ByteBuffer bb;
			Log.e(TAG, "time start ");
			long time = System.currentTimeMillis();
			long sum = 0;
			for (int i = 0; i < d; i++) {
				bb = ByteBuffer.wrap(b);
				// if (bb.remaining() <= 0) {
				// Log.e(TAG, "bb is null");
				// }
				sum += dc.send(bb, address);
			}
			time = System.currentTimeMillis() - time;
			Log.e(TAG, "" + (sum * 8000.0 / 1024 / time) + " kbps");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void JniUdp() {
		System.loadLibrary("udpSend");
		udpSend jni = new udpSend();
		jni.init(port);
		byte[] b = new byte[l];
		Log.e(TAG, "time start ");
		int sum = 0;
		int s = 0;
		long time = System.currentTimeMillis();
		for (int i = 0; i < d; i++) {
			s = jni.send(b);
			if (s > 0) {
				sum += s;
			}
		}
		time = System.currentTimeMillis() - time;
		Log.e(TAG, "" + (sum * 8000.0 / 1024 / time) + " kbps");
	}
}
