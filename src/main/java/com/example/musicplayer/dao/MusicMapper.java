package com.example.musicplayer.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.musicplayer.model.Music;

public class MusicMapper implements RowMapper<Music> {

	@Override
	public Music mapRow(ResultSet rs, int rowNum) throws SQLException {
		Music music = new Music();
		music.setId(rs.getInt("id"));
		music.setName(rs.getString("name"));
		music.setLocation(rs.getString("location"));
		music.setStatus(rs.getBoolean("status"));
		music.setSpecial(rs.getBoolean("special"));
		music.setPlaytime(rs.getTimestamp("playtime"));
		return music;
	}

}
