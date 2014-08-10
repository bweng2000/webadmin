package com.example.webadmin.ws;

import java.sql.Timestamp;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webadmin.dao.PlaylistDao;
import com.example.webadmin.dao.StoregroupDao;
import com.example.webadmin.model.Group;
import com.example.webadmin.model.Group.Category;
import com.example.webadmin.model.Store;

/**
 * This is the Jersey resource class that provides the store and group management services.
 * All CRUD operations are done through this resource class.
 * @author bweng
 *
 */
@Component	//Important note: As of Jersey 2.7, Jersey-Spring module only recognizes @Component as the bean injection. You can't use @Service annotation here.
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
	
	@Path("groups/category/{category}")
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
	
	@Path("groups/id/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGroupById(@PathParam("id") int groupId) {
		Group group;
		try {
			group = storegroupDao.getGroupById(groupId);
		} catch (DataAccessException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		String headerValue = "application/json; charset=utf-8";
		return Response.ok(group).header("Content-Type", headerValue).build();
	}
	
	@Path("groups/create")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createNewGroup(@FormParam("groupName") String groupName, @FormParam("groupCategory") String groupCategory,
			@FormParam("expireDate") Timestamp expireDate) {
		Response r = null;
		Category category = Category.valueOf(groupCategory);
		String headerValue = "application/json; charset=utf-8";
		try {
			storegroupDao.addNewGroup(groupName, category, expireDate);
			r = Response.ok().entity("创建新组成功").build();
		} catch (IllegalArgumentException ex) {
			r = Response.status(Status.BAD_REQUEST).entity("临时组过期日期不能为空").header("Content-Type", headerValue).build();
		} catch (DataAccessException ex) {
			r = Response.status(Status.BAD_REQUEST).entity("不能创建新组(组名不能重复)").header("Content-Type", headerValue).build();
		}
		return r;
	}
	
	@Path("groups/id/{id}")
	@DELETE
	public Response deleteGroupById(@PathParam("id") int groupID) {
		Group group = storegroupDao.getGroupById(groupID);
		String groupName = group.getGroupName();
		storegroupDao.removeGroup(groupName);
		return Response.status(Status.OK).build();
	}
	
	@Path("store/add/to/group")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public Response addStoresToGroup(@FormParam("storeName") List<String> storeNames, @FormParam("groupName") String groupName) {
		Store store;
		Group group = storegroupDao.getGroupByName(groupName);
		String headerValue = "text/plain; charset=utf-8";
		for (String name : storeNames) {
			store = storegroupDao.getStoreByName(name);
			/*if(!storegroupDao.addStoreToGroup(store, group)) {
				String errMsg = "门店: " + name + "不能放到" + groupName;
				throw new WebApplicationException(Response.status(Status.CONFLICT).entity(errMsg).header("Content-Type", headerValue).build());
			}*/
			try {
				storegroupDao.addStoreToGroup(store, group);
			} catch (DataAccessException ex) {
				String errMsg = "门店: " + name + "不能放到" + groupName;
				throw new WebApplicationException(Response.status(Status.CONFLICT).entity(errMsg).header("Content-Type", headerValue).build());
			}
		}
		return Response.ok().build();
	}
	
	@Path("store/remove/from/group")
	@DELETE
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public Response removeStoresFromGroup(@FormParam("storeName") List<String> storeNames, @FormParam("groupName") String groupName) {
		Store store;
		Group group = storegroupDao.getGroupByName(groupName);
		String headerValue = "text/plain; charset=utf-8";
		for (String name : storeNames) {
			store = storegroupDao.getStoreByName(name);
			if(!storegroupDao.removeStoreFromGroup(store, group)) {
				String errMsg = "门店: " + name + "不能从" + groupName + "中删除";
				throw new WebApplicationException(Response.status(Status.CONFLICT).entity(errMsg).header("Content-Type", headerValue).build());
			}
		}
		return Response.ok().build();
	}

}
