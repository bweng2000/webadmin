package com.example.musicplayer.model;

import java.sql.Timestamp;

public class Device {
	
	private long deviceID;
	private boolean connectStatus;
	private Timestamp timeReported;
	
	public long getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(long deviceID) {
		this.deviceID = deviceID;
	}
	public boolean getConnectStatus() {
		return connectStatus;
	}
	public void setConnectStatus(boolean connectStatus) {
		this.connectStatus = connectStatus;
	}
	public Timestamp getTimeReported() {
		return timeReported;
	}
	public void setTimeReported(Timestamp timeReported) {
		this.timeReported = timeReported;
	}
	
}
