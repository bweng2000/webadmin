package com.example.webadmin.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
	public void addToList(final List<Music> toAdd) {

		String sqlQuery = "INSERT INTO Music (name, status, special, playtime) VALUES (?, ?, ?, ?)";
		jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				Music music = toAdd.get(i);
				ps.setString(1, music.getName());
				ps.setBoolean(2, music.getStatus());
				ps.setBoolean(3, music.getSpecial());
				ps.setTimestamp(4, music.getPlaytime());
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

}
