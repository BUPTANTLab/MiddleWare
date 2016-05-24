package com.ANT.MiddleWare.Entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.ANT.MiddleWare.Entities.FileFragment.FileFragmentException;

import android.util.Log;

public class Segment {
	private static final String TAG = Segment.class.getSimpleName();

	private int segmentID;
	private ArrayList<FileFragment> segmentList;
	private int segLength = -1;
	private boolean Intergrity = false;
	private static Random random = new Random();
	private int percent = 0;
	private byte[] data = null;

	public synchronized void setSegLength(int segLength) {
		if (this.segLength == -1) {
			this.segLength = segLength;
			this.data = new byte[segLength];
		}
	}

	public Segment(int id, int length) {
		this.segmentID = id;
		this.segLength = length;
		if (this.segLength != -1) {
		this.data = new byte[segLength];}
		segmentList = new ArrayList<FileFragment>();
	}

	private boolean realeaseFragement(FileFragment fm) {
		if (data == null)
			return false;
		if (!fm.isWritten())
			return true;
		System.arraycopy(fm.getData(), 0, data, fm.getStartIndex(),
				fm.getFragLength());
		fm.realeaseData();
		return true;
	}

	public boolean insert(FileFragment fm) {
		synchronized (this) {
			if (fm.isWritten() && fm.getSegmentID() == segmentID && !Intergrity) {
				percent += fm.getFragLength();
				setSegLength(fm.getSegmentLen());
				boolean res = realeaseFragement(fm);
				if (!res)
					return false;
				segmentList.add(fm.clone());
				Collections.sort(segmentList);
				return true;
			}
			return false;
		}
	}

	private void merge() throws SegmentException, FileFragmentException {
		if (segmentList == null || segmentList.size() <= 1) {
			return;
		}
		int size = segmentList.size();
		for (int i = 0; i < size - 1; i++) {
			FileFragment prev = segmentList.get(i);
			FileFragment next = segmentList.get(i + 1);
			if (prev.getStopIndex() < next.getStartIndex()) {
				continue;
			}
			if (prev.getStartIndex() == next.getStartIndex()) {
				if (prev.getFragLength() <= next.getFragLength()) {
					segmentList.remove(i);
					percent -= prev.getFragLength();
				} else {
					segmentList.remove(i + 1);
					percent -= next.getFragLength();
				}
				size--;
				i--;
				continue;
			} else if (prev.getStartIndex() < next.getStartIndex()) {
				if (prev.getStopIndex() < next.getStopIndex()) {
					percent -= prev.getFragLength();
					prev.setData(next.getFragLength(), next.getStartIndex());
					percent += prev.getFragLength();
					Log.d(TAG, "" + segLength + " " + prev.getStopIndex());
				}
				segmentList.remove(i + 1);
				percent -= next.getFragLength();
				size--;
				i--;
			} else {
				throw new SegmentException("Not Sort");
			}
		}
	}

	public boolean checkIntegrity() {
		if (Intergrity)
			return Intergrity;
		synchronized (this) {
			if (segmentList == null) {
				return Intergrity;
			}
			try {
				merge();
			} catch (SegmentException e) {
				e.printStackTrace();
			} catch (FileFragmentException e) {
				e.printStackTrace();
			}

			if (segmentList.size() == 1
					&& segmentList.get(0).getFragLength() == segLength) {
				Intergrity = true;
				Log.w(TAG, "Percent " + getPercent());
				return Intergrity;
			}
			Log.w(TAG, "Percent " + getPercent());
			return Intergrity;
		}

	}

	public byte[] getData() {
		if (segmentList == null || segmentList.size() == 0)
			return null;
		checkIntegrity();
		int len = segmentList.get(0).getFragLength();
		int s = segmentList.get(0).getStartIndex();
		byte[] d = new byte[len];
		System.arraycopy(this.data, s, d, 0, len);
		return d;
	}

	public byte[] getData(int start) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (segmentList == null || segmentList.size() == 0) {
			return null;
		}
		checkIntegrity();
		synchronized (this) {
			for (FileFragment f : segmentList) {
				int len = f.getSegData(start);
				if (len <= 0)
					continue;
				byte[] buf = new byte[len];
				System.arraycopy(this.data, start, buf, 0, buf.length);
				return buf;
			}
			return null;
		}
	}

	public FileFragment getFragment(int start) throws FileFragmentException {
		byte[] data = getData(start);
		if (data == null)
			return null;
		FileFragment f = new FileFragment(start, start + data.length,
				segmentID, segLength);
		f.setData(data);
		return f;
	}

	public int getMiss() throws SegmentException {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (segmentList == null) {
			return 0;
		}
		int size = segmentList.size();
		if (size == 0) {
			return 0;
		}
		if (checkIntegrity())
			throw new SegmentException("No Fragment Miss");
		if (size == 1) {
			return segmentList.get(0).getStopIndex();
		}
		synchronized (this) {
			size = segmentList.size();
			return segmentList.get(random.nextInt(size - 1)).getStopIndex();
		}
	}

	public double getPercent() {
		return percent * 100.0 / segLength;
	}

	public class SegmentException extends Exception {
		private static final long serialVersionUID = 1187571347280690149L;

		public SegmentException(String string) {
			super(string);
		}
	}
}