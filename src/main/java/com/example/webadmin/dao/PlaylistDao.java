package com.example.webadmin.dao;

import java.io.File;
import java.util.List;

import com.example.model.Music;
import com.example.webadmin.model.Group;

public interface PlaylistDao {
	List<Music> getAllMusic();
	
	void changeList(List<Music> toAdd, List<Music> toRemove, List<Music> toUpdate);
	
	void addToList(List<Music> toAdd);

	void deleteFromList(List<Music> toDelete);
	
	void removeFromList(List<Music> toRemove);
	
	List<Music> getAllMusicFromGroup(Group group);
	
	boolean addMusicToGroup(Music music, Group group);
	
	boolean[] addAllMusicToGroup(List<Music> music, Group group);
	
	boolean removeMusicFromGroup(Music music, Group group);
	
	boolean[] removeAllMusicFromGroup(List<Music> music, Group group);
	
	List<Music> getMusicsFromNames(List<String> names);

}
