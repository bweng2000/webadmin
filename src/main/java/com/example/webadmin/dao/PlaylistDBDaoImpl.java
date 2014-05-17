package com.example.webadmin.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.dao.MusicMapper;
import com.example.model.Music;

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
	public List<Music> getAllList() {
		String sqlQuery = "SELECT * FROM Music";
		List<Music> playlist = jdbcTemplate.query(sqlQuery, new MusicMapper());
		return playlist;
	}

	@Override
	public void changeList(List<Music> toAdd, List<Music> toRemove,
			List<Music> toUpdate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addToList(List<Music> toAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFromList(List<Music> toDelete) {
		// TODO Auto-generated method stub

	}

}
