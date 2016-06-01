package com.ANT.MiddleWare.WiFi;

import java.io.IOException;
import java.util.LinkedList;

import android.content.Context;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;

public abstract class WiFiPulic {
	protected final static LinkedList<byte[]> taskList = 
			new LinkedList<byte[]>();
	protected Context contect;

	public WiFiPulic(Context contect) {
		this.contect = contect;
	}

	@SuppressWarnings("unused")
	private void WiFiPublic() {
	}

	public final void insertF(FileFragment fm) {
		if (fm.isTooBig()) {
			FileFragment[] fragArray = null;
//			Log.d("wifiBroadinsert", fm.toString()+" "+fm.getSegmentID());
			try {
				fragArray = fm.split();
			} catch (FileFragmentException e) {
				e.printStackTrace();
			}
			synchronized (taskList) {
				for (FileFragment f : fragArray) {
					taskList.add(f.toBytes());
				}
			}
		} else {
			synchronized (taskList) {
				taskList.add(fm.toBytes());
			}
		}
	}

	public abstract void notify(int seg, int start);

	public abstract void EmergencySend(byte[] data)
			throws FileFragmentException, IOException;

	public abstract void destroy() throws InterruptedException;
}
