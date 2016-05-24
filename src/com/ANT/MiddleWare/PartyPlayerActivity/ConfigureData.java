package com.ANT.MiddleWare.PartyPlayerActivity;

import com.ANT.MiddleWare.Celluar.CellularDown.CellType;

public class ConfigureData {
	private static final String TAG = ConfigureData.class.getSimpleName();

	public static enum WorkMode {
		LOCAL_MODE, G_MDOE, COOPERATIVE_MODE, JUNIT_TEST_MODE
	}
	
	private String url;
	private boolean serviceAlive = false;
	private WorkMode workingMode = WorkMode.LOCAL_MODE;

	private boolean noNotify = false;

	private boolean noEmeSend = false;

	private CellType DefMore = CellType.NOCELL;
	private CellType DefCell = CellType.NOCELL;
	private boolean noWiFiSend = true;
	private int filenum=-1;

	public ConfigureData(String url) {
		this.url = url;
		serviceAlive = false;
		workingMode = WorkMode.LOCAL_MODE;
	}
	
	public int getFileNum(){
		return filenum;
	}
	
	public void setFileNum(int i){
		this.filenum=i;
	}

	public WorkMode getWorkingMode() {
		return workingMode;
	}

	public void setWorkingMode(WorkMode workingMode) {
		this.workingMode = workingMode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isServiceAlive() {
		return serviceAlive;
	}

	public void setServiceAlive(boolean serviceAlive) {
		this.serviceAlive = serviceAlive;
	}

	public boolean isNoNotify() {
		return noNotify;
	}

	public void setNoNotify(boolean noNotify) {
		this.noNotify = noNotify;
	}

	public boolean isNoEmeSend() {
		return noEmeSend;
	}

	public void setNoEmeSend(boolean noEmeSend) {
		this.noEmeSend = noEmeSend;
	}

	public CellType getDefMore() {
		return DefMore;
	}

	public void setDefMore(CellType defMore) {
		DefMore = defMore;
	}

	public CellType getDefCell() {
		return DefCell;
	}

	public void setDefCell(CellType defCell) {
		DefCell = defCell;
	}

	public boolean isNoWiFiSend() {
		return noWiFiSend;
	}

	public void setNoWiFiSend(boolean noWiFiSend) {
		this.noWiFiSend = noWiFiSend;
	}

}
