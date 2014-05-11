package com.example.webadmin.dao;

import java.io.File;
import java.util.List;

public interface PlaylistDao {
	List<File> getActiveList();
	
	List<File> getInactiveList();
	
	public List<String> getActiveFileNames();
	
	public List<String> getInactiveFileNames();

	void addToList(List<String> newFiles);

	void deleteFromList(List<String> files);
}
