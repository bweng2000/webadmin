package com.example.webadmin.ws;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.example.webadmin.dao.PlaylistDao;
import com.example.webadmin.dao.StoregroupDao;
import com.example.webadmin.model.Group;
import com.example.webadmin.model.Group.Category;

@Path("/home")
public class StoreGroupService {
	@Autowired
	private StoregroupDao storegroupDao;
	
	@Path("groups")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGroups() {
		List<Group> groups = storegroupDao.getAllGroups();
		String headerValue = "application/json; charset=utf-8";
		return Response.ok(groups).header("Content-Type", headerValue).build();
	}
	
	@Path("groups/{category}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGroupsByCategory(@PathParam("category") String cat) {
		List<Group> groups;
		try {
			groups = storegroupDao.getGroupByCategory(Category.valueOf(cat));
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
		} catch (DataAccessException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		String headerValue = "application/json; charset=utf-8";
		return Response.ok(groups).header("Content-Type", headerValue).build();
	}

}
