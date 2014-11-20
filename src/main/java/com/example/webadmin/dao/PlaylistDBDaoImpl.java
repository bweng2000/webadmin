package com.example.webadmin.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.example.musicplayer.dao.MusicMapper;
import com.example.musicplayer.model.Music;
import com.example.webadmin.model.Group;

/**
 * This class is the DAO class that acccess the Music table in christine database.
 * This implements PlaylistDao interface. Different from {@code com.example.dao.PlaylistDAO}.
 * @author bweng
 *
 */
public class PlaylistDBDaoImpl implements PlaylistDao {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}*/
	
	@Override
	public List<Music> getAllMusic() {
		String sqlQuery = "SELECT * FROM Music";
		List<Music> playlist = jdbcTemplate.query(sqlQuery, new MusicMapper());
		return playlist;
	}
	
	@Override
	public List<Music> getActiveList() {
		String sqlQuery = "SELECT * FROM Music WHERE status=1";
		List<Music> playlist = jdbcTemplate.query(sqlQuery, new MusicMapper());
		return playlist;
	}

	@Override
	public void updateList(final List<Music> toUpdate) {
		String query = "UPDATE Music SET status=?, special=?, playtime=?";
		jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				Music music = toUpdate.get(i);
				ps.setBoolean(1, music.getStatus());
				ps.setBoolean(2, music.getSpecial());
				ps.setTimestamp(3, music.getPlaytime());
			}

			@Override
			public int getBatchSize() {
				return toUpdate.size();
			}
			
		});

	}

	@Override
	public void addToList(final List<Music> toAdd) {

		String sqlQuery = "INSERT INTO Music (name, location, status, special, playtime) VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				Music music = toAdd.get(i);
				ps.setString(1, music.getName());
				ps.setString(2, music.getLocation());
				ps.setBoolean(3, music.getStatus());
				ps.setBoolean(4, music.getSpecial());
				ps.setTimestamp(5, music.getPlaytime());
			}

			@Override
			public int getBatchSize() {
				return toAdd.size();
			}
			
		});

	}

	@Override
	public void deleteFromList(final List<Music> toDelete) {
		String sqlQuery = "DELETE FROM Music WHERE id=?";
		jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				Music music = toDelete.get(i);
				ps.setInt(1, music.getId());
			}

			@Override
			public int getBatchSize() {
				return toDelete.size();
			}
		});
	}

	@Override
	public void removeFromList(final List<Music> toRemove) {

		String sqlQuery = "UPDATE Music SET status=0 WHERE name=?";
		jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				Music music = toRemove.get(i);
				ps.setString(1, music.getName());
			}

			@Override
			public int getBatchSize() {
				return toRemove.size();
			}
			
		});
	}

	@Override
	public List<Music> getAllMusicFromGroup(Group group) {
		int groupID = group.getId();
		String query = "SELECT * FROM Music AS m INNER JOIN GroupMusic AS g WHERE g.groupID=? AND g.musicID=m.id ORDER BY m.id";
		List<Music> music = jdbcTemplate.query(query, new Object[] {groupID}, new MusicMapper());
		return music;
	}

	@Override
	public boolean addMusicToGroup(Music music, Group group) {
		int groupID = group.getId();
		int musicID = music.getId();
		String query = "INSERT INTO GroupMusic (groupID, musicID) VALUES (?, ?)";
		try {
			jdbcTemplate.update(query, new Object[] { groupID, musicID });
		} catch (DataAccessException ex) {
			return false;
		}
		return true;
	}

	@Override
	public boolean[] addAllMusicToGroup(List<Music> music, Group group) {
		int size = music.size();
		boolean[] successFlags = new boolean[size];
		for (int i = 0; i < size; i++) {
			successFlags[i] = addMusicToGroup(music.get(i), group);
		}
		return successFlags;

	}

	@Override
	public boolean removeMusicFromGroup(Music music, Group group) {
		int groupID = group.getId();
		int musicID = music.getId();
		String query = "DELETE FROM GroupMusic WHERE groupID=? AND musicID=?";
		try {
			jdbcTemplate.update(query, new Object[] {groupID, musicID});
		} catch (DataAccessException ex) {
			return false;
		}
		return true;
	}

	@Override
	public boolean[] removeAllMusicFromGroup(List<Music> music, Group group) {
		int size = music.size();
		boolean[] successFlags = new boolean[size];
		for(int i =0; i < size; i++) {
			successFlags[i] = removeMusicFromGroup(music.get(i), group);
		}
		return successFlags;
	}

	@Override
	public List<Music> getMusicsFromNames(List<String> names) {
		List<Music> musics = new ArrayList<Music>();
		Music singleItem;
		String query = "SELECT * FROM Music WHERE name=?";
		for(String name : names) {
			/*if(!name.endsWith(".mp3")) {
				name += ".mp3";
			}*/
			singleItem = jdbcTemplate.queryForObject(query, new Object[] {name}, new MusicMapper());
			musics.add(singleItem);
		}
		return musics;
	}
	
	@Override
	public File getFileById(int id) throws DataAccessException, FileNotFoundException {
		String sqlQuery = "SELECT name, location FROM Music WHERE id=?";
		Map<String, Object> result = jdbcTemplate.queryForMap(sqlQuery, new Object[] { id });
		String name = (String) result.get("name");
		String location = (String) result.get("location");

		if (!location.endsWith("/")) {
			location = location.concat("/");
		}
		File mediaFile = new File(location + name);
		if(!mediaFile.exists())
			throw new FileNotFoundException();
		return mediaFile;
	}

	@Override
	public void addNewMusicForStaging(Music music) throws DataAccessException {
		String query = "INSERT INTO Music (name, location, status, special, playtime) VALUES (?, ?, 0, 0, null)";
		String name = music.getName();
		String location = music.getLocation();
		jdbcTemplate.update(query, new Object[] {name, location});
	}

}
