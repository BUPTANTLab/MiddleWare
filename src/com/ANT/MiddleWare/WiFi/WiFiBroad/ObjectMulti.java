package com.ANT.MiddleWare.WiFi.WiFiBroad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.PipedInputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;
import com.ANT.MiddleWare.Entities.Segment;
import com.ANT.MiddleWare.Integrity.IntegrityCheck;
import com.ANT.MiddleWare.PartyPlayerActivity.MainFragment;
import com.ANT.MiddleWare.PartyPlayerActivity.ConfigureData.WorkMode;
import com.ANT.MiddleWare.WiFi.WiFiFactory;

public class ObjectMulti extends Thread {
	private static final String TAG = ObjectMulti.class.getSimpleName();

	private Context activity;
	private PipedInputStream pi;
	private SendMulti sm;

	public ObjectMulti(PipedInputStream pi, Context activity, SendMulti sm) {
		this.activity = activity;
		this.pi = pi;
		this.sm = sm;
	}

	@Override
	public void run() {
		ObjectInputStream oi;
		try {
			/*
			 * 
			 * http://docs.oracle.com/javase/6/docs/api/java/io/ObjectInputStream
			 * .html Creates an ObjectInputStream that reads from the specified
			 * InputStream. A serialization stream header is read from the
			 * stream and verified. This constructor will *** block *** until
			 * the corresponding ObjectOutputStream has written and flushed the
			 * header.
			 */
			oi = new ObjectInputStream(pi);
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		while (true) {
			try {
				final FileFragment ff = (FileFragment) oi.readObject();
				//测试收到时间写入文件
				String startOffset=String.valueOf(ff.getStartIndex());
				String stopOffset=String.valueOf(ff.getStopIndex());
				String segId=String.valueOf(ff.getSegmentID());
				SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss:SSS");
				Date curDate = new Date(System.currentTimeMillis());//获取当前时间
				String str = format.format(curDate);
				String receive="sId:"+segId+"\t start:"+startOffset+"\t stop:"+
				stopOffset+"\t time:"+System.currentTimeMillis()+"\t "+str+"\n";
				String dir=Environment.getExternalStorageDirectory()+"/lbroadtest/";
				File filedir=new File(dir);
				filedir.mkdir();
				int num = MainFragment.configureData.getFileNum();
//				if(filedir.isDirectory()){
//					String[] s =filedir.list();
//					num=s.length;
//				}
				File file=new File(dir, "lreceive_10k_sp0_"+num+".txt");
				if(!file.exists()){
					file.createNewFile();
				}
				FileOutputStream fos =new FileOutputStream(file, true);
				fos.write(receive.getBytes());
				fos.close();
				
				if (ff != null) {
					Log.d(TAG, ff.toString());
					if(ff.getStartIndex()==89){
					Log.d("rece51",String.valueOf(System.currentTimeMillis()));}
					if (MainFragment.configureData.getWorkingMode() != WorkMode.JUNIT_TEST_MODE) {
						((Activity) activity).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(activity, ff.toString(),
										Toast.LENGTH_SHORT).show();
							}
						});
					}
					switch (ff.getSegmentID()) {
					case WiFiBroad.FRAG_REQST_TAG:
						Segment s = IntegrityCheck.getInstance().getSeg(
								ff.getStartIndex());
						FileFragment f = s.getFragment(ff.getStopIndex());
						sm.removeRepeat(f);
						WiFiFactory.insertF(f);
						break;
					case WiFiBroad.EMERGEN_SEND_TAG:
						// RoundRobin.getInstance().insertToIPList(WiFiBroad.baseIP+ff.getStartIndex());
						// TODO
						// send IP
						// RoundRobin.getInstance().sendIP(WiFiBroad.baseIP+ff.getStartIndex());
//						if(ff.getStartIndex()==51){
//							WiFiFactory.EmergencySend("I am Captain!"
//									.getBytes("UTF-8"));
//							Log.d("send2",String.valueOf(System.currentTimeMillis()));}
						((Activity) activity).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(activity, "I am slave",
										Toast.LENGTH_SHORT).show();
							}
						});
						break;
					default:
						ff.check();
						IntegrityCheck.getInstance().insert(ff.getSegmentID(),
								ff);
						break;
					}
				}

			} catch (StreamCorruptedException e) {
				// Thrown when control information that was read from an object
				// stream violates internal consistency checks.
			} catch (OptionalDataException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (FileFragmentException e) {
				e.printStackTrace();
			}
		}
	}
}
