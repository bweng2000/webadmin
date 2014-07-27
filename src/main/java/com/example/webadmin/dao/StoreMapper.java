package com.example.webadmin.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.webadmin.model.Store;

public class StoreMapper implements RowMapper<Store> {

	@Override
	public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
		Store store = new Store();
		store.setStoreID(rs.getInt("storeID"));
		store.setStoreName(rs.getString("storeName"));
		store.setDeviceID(rs.getLong("deviceID"));
		return store;
	}

}
