package com.example.webadmin.dao;

import java.io.File;
import java.util.List;

import com.example.model.Music;

public interface PlaylistDao {
	List<Music> getAllList();
	
	void changeList(List<Music> toAdd, List<Music> toRemove, List<Music> toUpdate);
	
	void addToList(List<Music> toAdd);

	void deleteFromList(List<Music> toDelete);
	
	void removeFromList(List<Music> toRemove);
}
