package com.example.musicplayer.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.example.musicplayer.model.Music;

public interface PlaylistDAO {
	List<Music> getActiveList(long deviceID);
	
	File getFileById(int id) throws DataAccessException, FileNotFoundException;
}
