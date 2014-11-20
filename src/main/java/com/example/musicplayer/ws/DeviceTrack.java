package com.example.musicplayer.ws;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.example.musicplayer.dao.DeviceDAO;
import com.example.musicplayer.model.Device;

/**
 * This is the web service resource class
 * to handle the device status update. It accepts JSON
 * as the media data format.
 * @author bweng
 *
 */
@Component
@Path("/device")
public class DeviceTrack {
	
	@Autowired
	private DeviceDAO dao;
	
	/*public DeviceTrack(@Context HttpServletRequest request) {
		WebApplicationContext context = WebApplicationContextUtils.
				getRequiredWebApplicationContext(request.getSession().getServletContext());
		dao = (DeviceDAO) context.getBean(DeviceDAO.class);
	}*/
	
	@Path("/update")
	@GET
	//@Consumes(MediaType.APPLICATION_JSON)
	//@Consumes(MediaType.TEXT_PLAIN)
	public Response updateDeviceStatus(@QueryParam("deviceID") long deviceID) {
		Timestamp updateTime = new Timestamp(new Date().getTime());	//get the current update time
		try {
			dao.updateStatus(deviceID, updateTime);
			return Response.ok().build();
		} catch(DataAccessException e) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).build());
		}
	}

}
