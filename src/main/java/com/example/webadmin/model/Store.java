package com.example.webadmin.model;

import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Store {
	private String storeID;
	private String storeName;
	private long deviceID;
	@JsonIgnore
	private Set<Group> groups;
	
	public String getStoreID() {
		return storeID;
	}
	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public long getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(long deviceID) {
		this.deviceID = deviceID;
	}
	public Set<Group> getGroups() {
		return groups;
	}
	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
}
