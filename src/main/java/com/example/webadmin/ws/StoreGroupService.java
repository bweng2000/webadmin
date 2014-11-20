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
	private String headerValue = "application/json; charset=utf-8";
	
	@Path("groups")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGroups() {
		List<Group> groups = storegroupDao.getAllGroups();
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
		return Response.ok(group).header("Content-Type", headerValue).build();
	}
	
	@Path("groups/create")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createNewGroup(@FormParam("groupName") String groupName, @FormParam("groupCategory") String groupCategory,
			@FormParam("expireDate") Timestamp expireDate) {
		Response r = null;
		if(groupCategory.endsWith("组")) {
			groupCategory = groupCategory.substring(0, groupCategory.length()-1);
		}
		Category category = Category.valueOf(groupCategory);

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
	
	@Path("groups/delete")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response deleteGroupsByNames(List<String> groupNames) {
		Response r;
		try {
			storegroupDao.removeSomeGroups(groupNames);
			r = Response.ok().build();
		} catch (DataAccessException e) {
			r = Response.status(Status.BAD_REQUEST).build();
		}
		return r;
	}
	
	@Path("store/add/to/group")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public Response addStoresToGroup(@FormParam("storeName") String storeName, @FormParam("groupName") String groupName) {
		Store store;
		String[] storeNames = parseJSArray(storeName);
		Group group = storegroupDao.getGroupByName(groupName);
		String headerValue = "text/plain; charset=utf-8";
		for (String name : storeNames) {
			store = storegroupDao.getStoreByName(name);

			try {
				storegroupDao.addStoreToGroup(store, group);
			} catch (DataAccessException ex) {
				String errMsg = "门店: " + name + "不能放到" + groupName;
				throw new WebApplicationException(Response.status(Status.CONFLICT).entity(errMsg).header("Content-Type", headerValue).build());
			}
		}
		return Response.ok("添加成功").build();
	}
	
	@Path("store/remove/from/group")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public Response removeStoresFromGroup(@FormParam("storeName") String storeName, @FormParam("groupName") String groupName) {
		Store store;
		String[] storeNames = parseJSArray(storeName);
		Group group = storegroupDao.getGroupByName(groupName);
		String headerValue = "text/plain; charset=utf-8";
		for (String name : storeNames) {
			store = storegroupDao.getStoreByName(name);
			
			try {
				storegroupDao.removeStoreFromGroup(store, group);
			} catch (DataAccessException ex) {
				String errMsg = "门店: " + name + "不能从" + groupName + "中删除";
				throw new WebApplicationException(Response.status(Status.CONFLICT).entity(errMsg).header("Content-Type", headerValue).build());
			}
		}
		return Response.ok("删除成功").build();
	}

	@Path("store/{groupName}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStoresByGroupName(@PathParam("groupName") String groupName) {
		Response r;
		try {
			Group group = storegroupDao.getGroupByName(groupName);
			List<Store> stores = storegroupDao.getStoresFromGroup(group);
			r = Response.ok().entity(stores).header("Content-Type", headerValue).build();
		} catch (DataAccessException e) {
			r = Response.status(Status.NOT_FOUND).build();
		}
		return r;
	}
	
	@Path("store/unassigned")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStoreUnAssigned() {
		Response r;
		try {
			List<Store> unassigned = storegroupDao.getStoreUnassigned();
			r = Response.ok().entity(unassigned).header("Content-Type", headerValue).build();
		} catch(DataAccessException e) {
			r = Response.status(Status.NOT_FOUND).build();
		}
		return r;
	}
	
	/**
	 * This is a helper method to split a JS string array to individual string.
	 * @param jsArray input JS array
	 * @return array of strings
	 */
	private String[] parseJSArray(String jsArray) {
		String[] elements = jsArray.split("[,\\[\\]]");
		return elements;
	}
}
