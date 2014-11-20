package com.example.musicplayer.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.musicplayer.model.Device;

public class DeviceMapper implements RowMapper<Device> {

	@Override
	public Device mapRow(ResultSet rs, int rowNum) throws SQLException {
		Device device = new Device();
		device.setDeviceID(rs.getLong("deviceID"));
		device.setConnectStatus(rs.getBoolean("connectStatus"));
		device.setTimeReported(rs.getTimestamp("timeReported"));
		return device;
	}

}
