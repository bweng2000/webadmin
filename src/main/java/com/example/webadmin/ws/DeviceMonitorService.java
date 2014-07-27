package com.example.webadmin.ws;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.webadmin.dao.DevicelistDao;

@Path("/musicdevice")
public class DeviceMonitorService {

	private static final long TIME_DIFF_THRESH = 600000;	//10 minutes
	
	@Autowired
	private DevicelistDao devicelistDao;
	
	@Path("/monitor")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response monitorStatus() {
		List<Map<String, Object>> devicelist = devicelistDao.getDeviceStatus();
		
		long now = new Date().getTime();
		for(Map<String, Object> m : devicelist) {
			Timestamp timeReported = (Timestamp) m.get("timeReported");
			long updateTime = timeReported.getTime();
			if(Math.abs(updateTime - now) > TIME_DIFF_THRESH) {
				m.put("connectStatus", false);
			}
		}
		
		String headerValue = "application/json; charset=utf-8";
		return Response.ok().entity(devicelist).header("Content-Type", headerValue).build();
	}
}
