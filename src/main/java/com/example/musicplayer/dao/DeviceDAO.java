package com.example.musicplayer.dao;

import java.sql.Timestamp;

public interface DeviceDAO {
	public void updateStatus(long deviceID, Timestamp newTime);

}
