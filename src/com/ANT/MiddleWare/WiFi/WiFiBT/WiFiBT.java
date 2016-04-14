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

import com.ANT.MiddleWare.PartyPlayerActivity.MainFragment;
import com.ANT.MiddleWare.PartyPlayerActivity.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ANT.MiddleWare.WiFi.WiFiPulic;

public class WiFiBT extends WiFiPulic {
	private static final String TAG = WiFiBT.class.getSimpleName();
	private Context context=null;
	private TelephonyManager tm;
	private Process proc;
	private String ip ;
	private WifiManager wifi;
	private static ExecutorService es = Executors.newCachedThreadPool();
	private  ButtonInterface buttonListener = null;
	public interface ButtonInterface{
		public void onClick();
	}
	
	public WiFiBT(Context contect) {
		super(contect);
		this.context = contect;
//		View view = LayoutInflater.from(context).inflate(R.layout.fragment_main, null);
//		Button button = (Button) view.findViewById(R.id.btCaptain);
//		button.setText("B");
		makeToast("I am BT");
		tm = (TelephonyManager) contect
				.getSystemService(Activity.TELEPHONY_SERVICE);
		
		String s = tm.getDeviceId();
		int len = s.length();
		int number = Integer.parseInt(s.substring(len - 2));
		ip = "192.168.1." + number;
		Log.v(TAG, "ip " + ip);
		try {
			proc = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(proc.getOutputStream());
			os.writeBytes("netcfg wlan0 up\n");
			os.writeBytes("wpa_supplicant -iwlan0 -c/data/misc/wifi/wpa_supplicant.conf -B\n");
			os.writeBytes("ifconfig wlan0 " + ip + " netmask 255.255.255.0\n");
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
    private  void listen() {
        try {
            InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(ip), 12345);
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
    public  static void ask(String ip,int port ){
    	int remotePort = port;
    	try {
			InetAddress remoteAdr = InetAddress.getByName(ip);
		      SocketChannel sc=null;
		        try {
		                sc = SocketChannel.open();
		            sc.connect(new InetSocketAddress(remoteAdr, remotePort));
		            System.out.println("try to connect");
		                if (sc.isConnected()) {
		                    System.out.println("connection start");
		                    
		                    PeerMessage msgObj = new PeerMessage();
		                    msgObj.setMessage("I am client");
		                    WriteThread wt = new WriteThread(sc,msgObj);
		                    es.execute(wt);
		                    try {
		                        wt.join();
		                        System.out.println("ready to send");
		                    } catch (InterruptedException e) {
		                        e.printStackTrace();
		                    }
		                }
		        } catch (IOException e) {
		            e.printStackTrace();
		            System.out.println("connect fail");
		        }finally {
		            if (sc!=null&&(sc.isConnected())) {

//		                    sc.close();
		                    System.out.println("sc connected");

		            }
		            Thread.yield();
		        }
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

}
