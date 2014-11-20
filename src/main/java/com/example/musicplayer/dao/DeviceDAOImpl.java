package com.example.musicplayer.dao;

import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.musicplayer.model.Device;

public class DeviceDAOImpl implements DeviceDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private DataSource dataSource;
	
	@Override
	public void updateStatus(long deviceID, Timestamp newTime) {
		String selectQuery = "SELECT * FROM Device WHERE deviceID=?";
		String createQuery = "INSERT INTO Device (deviceID, connectStatus, timeReported) VALUES(?, 1, ?)";
		String updateQuery = "UPDATE Device SET connectStatus=1, timeReported=? WHERE deviceID=?";
		
		List<Device> device = jdbcTemplate.query(selectQuery, new Object[] {deviceID}, new DeviceMapper());
		if (device.isEmpty()) {
			jdbcTemplate.update(createQuery, new Object[] {deviceID});
		} else {
			jdbcTemplate.update(updateQuery, new Object[] {newTime, deviceID});			
		}
	}

}
