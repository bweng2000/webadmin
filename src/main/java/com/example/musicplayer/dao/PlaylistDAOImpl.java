package com.example.musicplayer.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.musicplayer.model.Music;

/**
 * Concrete DAO class for Music table data
 * This class is used to get the current active
 * playlist and individual media file from DB.
 * @author bweng
 *
 */
public class PlaylistDAOImpl implements PlaylistDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}*/

	@Override
	public List<Music> getActiveList(long deviceID) {
		String sqlQuery = "SELECT * FROM Music WHERE status=1";
		List<Music> playlist = jdbcTemplate.query(sqlQuery, new MusicMapper());
		return playlist;
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

}
