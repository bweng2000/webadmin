package com.example.webadmin.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.dao.FileSystem;

/**
 * This class is the DAO class to access playlist via file system read/write
 * @author bweng
 *
 */
public class PlaylistFSDao implements PlaylistDao {
	
	private FileSystem activeFolder;
	private FileSystem inactiveFolder;
	
	public PlaylistFSDao(FileSystem active, FileSystem inactive) {
		this.activeFolder = active;
		this.inactiveFolder = inactive;
	}

	public List<File> getActiveList() {
		File[] files = activeFolder.getAllFiles();
		return Arrays.asList(files);
	}

	public List<File> getInactiveList() {
		File[] files = inactiveFolder.getAllFiles();
		return Arrays.asList(files);
	}

	public void addToList(List<String> newFiles) {
		

	}

	public void deleteFromList(List<String> files) {
		// TODO Auto-generated method stub

	}
	
	public List<String> getActiveFileNames() {
		File[] files = activeFolder.getAllFiles();
		List<String> names = new ArrayList<String>();
		for(File f : files) {
			names.add(f.getName());
		}
		return names;
	}

	public List<String> getInactiveFileNames() {
		File[] files = inactiveFolder.getAllFiles();
		List<String> names = new ArrayList<String>();
		for(File f : files) {
			names.add(f.getName());
		}
		return names;
	}
}
