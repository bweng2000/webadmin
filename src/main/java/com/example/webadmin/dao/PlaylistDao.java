package com.example.webadmin.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.example.musicplayer.model.Music;
import com.example.webadmin.model.Group;

public interface PlaylistDao {
	List<Music> getAllMusic();
	
	void updateList(List<Music> toUpdate);
	
	void addToList(List<Music> toAdd);
	
	void addNewMusicForStaging(Music music);

	void deleteFromList(List<Music> toDelete);
	
	void removeFromList(List<Music> toRemove);
	
	List<Music> getAllMusicFromGroup(Group group);
	
	boolean addMusicToGroup(Music music, Group group);
	
	boolean[] addAllMusicToGroup(List<Music> music, Group group);
	
	boolean removeMusicFromGroup(Music music, Group group);
	
	boolean[] removeAllMusicFromGroup(List<Music> music, Group group);
	
	List<Music> getMusicsFromNames(List<String> names);

	File getFileById(int id) throws DataAccessException, FileNotFoundException;

	List<Music> getActiveList();

}
