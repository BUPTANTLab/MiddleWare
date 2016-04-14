package com.ANT.MiddleWare.WiFi.WiFiBT;

public class PeerMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7004811480749043877L;
	private String msg;
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return msg;
	}

	@Override
	public void setMessage(String msg) {
		// TODO Auto-generated method stub
		this.msg = msg;
	}

}
