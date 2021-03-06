package com.example.webadmin.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import com.example.musicplayer.model.Device;
import com.example.webadmin.model.Group;
import com.example.webadmin.model.Group.Category;
import com.example.webadmin.model.Store;

public interface StoregroupDao {
	public List<Group> getAllGroups();
	
	public List<Group> getGroupByCategory(Group.Category cat);
	
	public Group getGroupById(int groupID);
	
	public Group getGroupByName(String groupName);
	
	public List<Store> getStoresFromGroup(Group group);
	
	public void addStoreToGroup(Store store, Group group);
	
	public void removeStoreFromGroup(Store store, Group group);
	
	public int addNewGroup(String groupName, Category category, Timestamp expireDate);
	
	public void removeGroup(String groupName);
	
	void removeSomeGroups(List<String> groupNames);
	
	/**
	 * Get all groups of a store. A store may belong to several groups, but each store can only belong to one group in a group category.
	 * @param store The given store
	 * @return A set of groups that the store belongs to
	 */
	public List<Group> getGroupsOfStore(Store store);
	
	public Store getStoreByName(String name);
	
	public Store getStorebyId(String id);
	
	public Store addNewStore(String id, String name, long deviceID);
	
	public Store getStoreByDeviceID(long deviceID);
	
	List<Store> getStoreUnassigned();

}
