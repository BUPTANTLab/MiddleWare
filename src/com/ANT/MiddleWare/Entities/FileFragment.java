package com.ANT.MiddleWare.Entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import android.util.Log;

public class FileFragment implements Comparable<FileFragment>, Serializable,
		Cloneable {
	private static final long serialVersionUID = -7869356592846635319L;

	private static final String TAG = FileFragment.class.getSimpleName();
	private static boolean TRY_LESS_GC = false;// Almost Same??
	public static int LIMIT_LEN = 16 * 1024;

	private int startIndex;
	private int stopIndex;
	private int segmentID;
	private int segmentLen;
	private byte[] data = null;
	private boolean written = false;

	public FileFragment(int start, int stop, int segID, int seglen) {
		this.startIndex = start;
		this.stopIndex = stop;
		this.segmentID = segID;
		this.segmentLen = seglen;
	}

	public FileFragment(FileFragment fm) throws FileFragmentException {
		this.startIndex = fm.getStartIndex();
		this.stopIndex = fm.getStopIndex();
		this.segmentID = fm.getSegmentID();
		this.segmentLen = fm.getSegmentLen();
		int fragLength = fm.getFragLength();
		this.data = new byte[fragLength];
		this.setData(fm.getData());
	}

	public byte[] getData() {
		if (data != null)
			return data.clone();
		return null;
	}

	public int getSegData(int start) {
		if (start < startIndex || start >= stopIndex)
			return 0;
		int len = Math.min(stopIndex - start, LIMIT_LEN);
		return len;
	}

	public byte[] getData(int start) {
		if (start < startIndex || start >= stopIndex || !written)
			return null;
		int len = Math.min(stopIndex - start, LIMIT_LEN);
		byte[] buf = new byte[len];
		System.arraycopy(this.data, start - startIndex, buf, 0, buf.length);
		return buf;
	}

	public void setData(byte[] d) throws FileFragmentException {
		if (data == null) {
			int fragLength = stopIndex - startIndex;
			this.data = new byte[fragLength];
		}
		if (data.length != d.length)
			throw new FileFragmentException("Fragment Length Wrong "
					+ data.length + " " + d.length);
		System.arraycopy(d, 0, this.data, 0, d.length);
		this.written = true;
	}

	public boolean isWritten() {
		return written;
	}

	public int getFragLength() {
		return data.length;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getSegmentLen() {
		return segmentLen;
	}

	public int getStopIndex() {
		return stopIndex;
	}

	public int getSegmentID() {
		return segmentID;
	}

	public void check() throws FileFragmentException {
		if ((stopIndex - startIndex != data.length) || segmentID < 1
				|| written == false)
			throw new FileFragmentException("Fragment Check Fail!");
	}

	@Override
	public int compareTo(FileFragment another) {
		return Integer.valueOf(this.getStartIndex()).compareTo(
				Integer.valueOf(another.getStartIndex()));
	}

	@Override
	public String toString() {
		if (data == null) {
			return "Frag " + startIndex + " " + stopIndex + " 0";
		} else {
			return "Frag " + startIndex + " " + stopIndex + " " + data.length;
		}
	}

	@Override
	public FileFragment clone() {
		FileFragment o = null;
		try {
			o = (FileFragment) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + startIndex;
		result = 31 * result + stopIndex;
		result = 31 * result + segmentID;
		result = 31 * result + segmentLen;
		result = 31 * result + (written ? 1 : 0);
		result = 31 * result + Arrays.hashCode(data);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		FileFragment f = (FileFragment) obj;
		if (f == null) {
			return false;
		} else if (f.hashCode() != this.hashCode()) {
			return false;
		} else if (f.startIndex != this.startIndex) {
			return false;
		} else if (f.stopIndex != this.stopIndex) {
			return false;
		} else if (f.segmentID != this.segmentID) {
			return false;
		} else if (f.segmentLen != this.segmentLen) {
			return false;
		} else if (f.written != this.written) {
			return false;
		} else if (!Arrays.equals(f.data, this.data)) {
			return false;
		} else {
			return true;
		}
	}

	public byte[] toBytes() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			byte[] b = bos.toByteArray();
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
			}
			try {
				bos.close();
			} catch (IOException ex) {
			}
		}
		return null;
	}

	public FileFragment[] split() throws FileFragmentException {
		FileFragment base = this.clone();
		int piece = (int) Math.ceil(base.getFragLength() * 1.0 / LIMIT_LEN);
		if (piece == 1) {
			return new FileFragment[] { base };
		}
		FileFragment[] ff = new FileFragment[piece];
		for (int i = 0; i < piece; i++) {
			int start = LIMIT_LEN * i;
			int len = Math.min(LIMIT_LEN, base.getFragLength() - start);
			start += base.getStartIndex();
			ff[i] = new FileFragment(start, len + start, base.getSegmentID(),
					base.getSegmentLen());
			byte[] newdata = base.getData(start);
			if (newdata == null)
				throw new FileFragmentException("data null");
			ff[i].setData(newdata);
		}
		return ff;
	}

	public boolean isTooBig() {
		if (!written)
			return false;
		return data.length > LIMIT_LEN;
	}

	public class FileFragmentException extends Exception {

		private static final long serialVersionUID = -7646261501131644643L;

		public FileFragmentException(String string) {
			super(string);
		}
	}

	public void realeaseData() {
		data = null;
		written = false;
	}
}