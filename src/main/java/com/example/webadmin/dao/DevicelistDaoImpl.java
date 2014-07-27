package com.example.webadmin.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class DevicelistDaoImpl implements DevicelistDao {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Map<String, Object>> getDeviceStatus() {
		String sqlQuery = "SELECT d.*, s.storeName FROM Device AS d INNER JOIN Store AS s ON d.deviceID=s.deviceID";
		List<Map<String, Object>> deviceStatusInfo = jdbcTemplate.queryForList(sqlQuery);
		return deviceStatusInfo;
	}

}
