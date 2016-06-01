package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.PartyPlayerActivity.MainFragment;

public class SendMulti extends Thread {
	private static final String TAG = SendMulti.class.getSimpleName();

	private MulticastSocket socket;
	private LinkedList<byte[]> taskList;
	public HashSet<Integer> repeat = new HashSet<Integer>();

	public SendMulti(MulticastSocket mSocket, LinkedList<byte[]> mTaskList) {
		this.socket = mSocket;
		this.taskList = mTaskList;
	}

	public boolean isRepeat(byte[] f) {
		// if--judge whether f is notify message 
		if (f.length > 3000) {
			int hash = Arrays.hashCode(f);
			synchronized (repeat) {
				if (repeat.contains(hash)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static int pseudoHash(byte[] f) {
		int result = 17;
		for (byte l:f) {
			result = 31 * result + (int) l;
		}
		return result;
	}

	public void addRepeat(byte[] f) {
		// if--judge whether f is notify message 
		if (f.length > 3000) {
			int hash = pseudoHash(f);
			synchronized (repeat) {
				repeat.add(hash);
			}
		}
	}

	public void removeRepeat(FileFragment f) {
		if (f.getSegmentID() > 0) {
			byte[] ff = f.toBytes();
			int hash = pseudoHash(ff);
			synchronized (repeat) {
				repeat.remove(hash);
			}
		}
	}

	private synchronized boolean send(byte[] f) {
		if (isRepeat(f)) {
			return true;
		}
		if (MainFragment.configureData.isNoWiFiSend())
			return true;
		try {
//			byte[] data = f.toBytes();
			//���Է���ʱ��д���ļ�
//			String startOffset=String.valueOf(f.getStartIndex());
//			String stopOffset=String.valueOf(f.getStopIndex());
//			String segId=String.valueOf(f.getSegmentID());
//			String flen=String.valueOf(f.getStopIndex()-f.getStartIndex());
//			SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss:SSS");
//			Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
//			String str = format.format(curDate);
//			String send="sId:"+segId+"\t start:"+startOffset+"\t stop:"
//			+stopOffset+"\t time:"+System.currentTimeMillis()+"\t "+str+"\n";
//			String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/lbroadtest/";
//			File filedir=new File(dir);
//			filedir.mkdir();
//			int num=MainFragment.configureData.getFileNum();
//			File file=new File(dir, "lsend_10k_sp0_"+num+".txt");
//			if(!file.exists()){
//				file.createNewFile();
//			}
//			FileOutputStream fos =new FileOutputStream(file, true);
//			fos.write(send.getBytes());
//			fos.close();
			
			DatagramPacket dp = new DatagramPacket(f, f.length,
					InetAddress.getByName(WiFiBroad.multicastHost),
					//InetAddress.getByName("192.168.1.111"),
					WiFiBroad.localPort);
//			Log.d(TAG, "send "+startOffset+" "+stopOffset+" "+flen+" "+segId+" "+System.currentTimeMillis());
			socket.send(dp);
			addRepeat(f);
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		while (true) {
//			try {
//				Thread.sleep(90);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//				if (this.isInterrupted()) {
//					return;
//				}
//			}
//			if (!RoundRobin.getInstance().canITalk()) {
//				continue;
//			}
			
			byte[] ff = null;
			synchronized (taskList) {
				
				if (taskList.isEmpty()) {
					//RoundRobin.getInstance().passToken();
					continue;
				}
				ff = taskList.pop();
//				Log.d("localsend",ff.toString()+" "+ff.getSegmentID()+" "+System.currentTimeMillis());
			}
			if (ff == null) {
				continue;
			}
//			if (ff.isTooBig()) {
//				Log.d("sendtoobig", ff.toString()+" "+ff.getSegmentID());
//				WiFiFactory.insertF(ff);
			else {
				boolean is = send(ff);
				if (!is) {
//					Log.d("sendfail", ff.toString()+" "+ff.getSegmentID());
					synchronized (taskList) {
						taskList.addFirst(ff);
					}
				}
			}
		}
	}
}
