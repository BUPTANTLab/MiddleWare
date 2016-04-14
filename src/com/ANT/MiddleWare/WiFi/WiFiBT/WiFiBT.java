package com.ANT.MiddleWare.WiFi.WiFiBT;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ANT.MiddleWare.WiFi.WiFiPulic;

public class WiFiBT extends WiFiPulic {
	private static final String TAG = WiFiBT.class.getSimpleName();
	private Context context=null;
	private TelephonyManager tm;
	private Process proc;
	private WifiManager wifi;
	private static ExecutorService es = Executors.newCachedThreadPool();
	public WiFiBT(Context contect) {
		super(contect);
		this.context = contect;
		makeToast("I am BT");
		tm = (TelephonyManager) contect
				.getSystemService(Activity.TELEPHONY_SERVICE);

		//pi.connect(po);
		//po.connect(pi);
		String s = tm.getDeviceId();
		int len = s.length();
		int number = Integer.parseInt(s.substring(len - 2));
		String ip = "192.168.1." + number;
		makeToast(ip);
		Log.v(TAG, "ip " + ip);
		try {
			proc = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(proc.getOutputStream());
			os.writeBytes("netcfg wlan0 up\n");
			os.writeBytes("wpa_supplicant -iwlan0 -c/data/misc/wifi/wpa_supplicant.conf -B\n");
			os.writeBytes("ifconfig wlan0 " + ip + " netmask 255.255.255.0\n");
//			os.writeBytes("ip route add 192.168.1.1/24 dev wlan0\n");
			os.writeBytes("exit\n");
			os.flush();
			proc.waitFor();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					listen();
				}
			}).start();
			ask();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wifi = (WifiManager) contect.getSystemService(Context.WIFI_SERVICE);
		if (wifi != null) {
			WifiManager.WifiLock lock = wifi
					.createWifiLock("Log_Tag");
			lock.acquire();
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notify(int seg, int start) {
		// TODO Auto-generated method stub

	}

	@Override
	public void EmergencySend(byte[] data) {
		// TODO Auto-generated method stub
		
	}
	
	private void makeToast(String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
    private static void listen() {
        try {
            InetSocketAddress addr = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 12345);
            boolean condition = true;
            Selector selector = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(addr);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (condition) {
                int readyChannel = selector.select();
                if(readyChannel==0) continue;
                Set<SelectionKey> selectedChannel = selector.selectedKeys();
                Iterator ite = selectedChannel.iterator();
                while (ite.hasNext()) {
                    SelectionKey mKey = (SelectionKey) ite.next();
                    if (mKey.isAcceptable()) {          
                        SocketChannel ss = ((ServerSocketChannel)mKey.channel()).accept();
                        es.execute(new ReadThread(ss));
                    }
                    ite.remove();
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void ask( ){
    	int remotePort = 12345;
    	
    	InetSocketAddress addr = new InetSocketAddress("19", remotePort);
    }
}
