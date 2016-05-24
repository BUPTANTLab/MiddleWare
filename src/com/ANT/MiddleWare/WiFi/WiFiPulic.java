package com.ANT.MiddleWare.WiFi;

import java.io.IOException;
import java.util.Stack;

import android.content.Context;
import android.util.Log;

import com.ANT.MiddleWare.Entities.FileFragment;
import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;

public abstract class WiFiPulic {
	protected final static Stack<FileFragment> taskList = new Stack<FileFragment>();
	protected Stack<FileFragment> convertStack= new Stack<FileFragment>();
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
					taskList.add(f);
				}
				while (!taskList.empty()) {
//			         Log.d("convert", "before enqueue,taskList size: " + String.valueOf(taskList.size()));
			           FileFragment ff = taskList.pop();
			           convertStack.add(ff);
			    }
			}
		} else {
			synchronized (taskList) {
				taskList.add(fm);
			}
		}
	}

	public abstract void notify(int seg, int start);

	public abstract void EmergencySend(byte[] data)
			throws FileFragmentException, IOException;

	public abstract void destroy() throws InterruptedException;
}
