package com.example.webadmin.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.example.musicplayer.model.Music;
import com.example.webadmin.model.Group;

public class StoreGroupServiceTest {
	
	private Client client;
	
	@Before
	public void init() {
		//Here we need to register Jackson JSON POJO mapping feature for Jersey client
		client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
	}

	
	@Ignore
	@Test
	public void testCreateTempGroup() {
		WebTarget webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/groups/create");
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("groupName", "特殊组");
		formData.add("groupCategory", "临时");
		//formData.add("expireDate", "2014-09-04T12:05:00.000-0400");	//This does NOT work for Jersey Jackson JSON.
		formData.add("expireDate", "2014-09-04 12:35:00");
		
		Response response = webTarget.request().post(Entity.form(formData));
		String message = response.readEntity(String.class);
		System.out.println(message);
	}
	
	@Ignore
	@Test
	public void testGroup() {
		WebTarget webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/groups/create");
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("groupName", "崇明区");
		formData.add("groupCategory", "区域");
		formData.add("expireDate", null);
		
		Response response = webTarget.request().post(Entity.form(formData));
		String message = response.readEntity(String.class);
		System.out.println(message);
		
		formData.clear();
		formData.add("groupName", "临时测试组");
		formData.add("groupCategory", "临时");
		formData.add("expireDate", null);
		
		response = webTarget.request().post(Entity.form(formData));
		message = response.readEntity(String.class);
		System.out.println(message);
		
		formData.clear();
		formData.add("groupName", "长宁区");
		formData.add("groupCategory", "区域");
		formData.add("expireDate", null);
		
		response = webTarget.request().post(Entity.form(formData));
		message = response.readEntity(String.class);
		System.out.println(message);
		
		webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/groups/id");
		response = webTarget.path("1013").request().delete();
		int statusCode = response.getStatus();
		System.out.println(statusCode);
		
		webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/groups");
		response = webTarget.request(MediaType.APPLICATION_JSON).get();
		List<Group> groups = response.readEntity(new GenericType<List<Group>>(){});
		System.out.println(groups);
	}
	
	@Ignore
	@Test
	public void testStoreGroupAdd() {
		WebTarget webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/store/add/to/group");
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		List<String> storeName = new ArrayList<String>();
		storeName.add("重庆北路店");
		storeName.add("山海关路店");
		storeName.add("人民广场店");
		
		formData.addAll("storeName", storeName);
		formData.add("groupName", "静安区");
		
		Response response = webTarget.request().accept(MediaType.TEXT_PLAIN).post(Entity.form(formData));
		String message = response.readEntity(String.class);
		System.out.println(message);
	}
	
	@Ignore
	@Test
	public void testStoreGroupRemove() {
		WebTarget webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/store/remove/from/group");
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		List<String> storeName = new ArrayList<String>();
		storeName.add("重庆北路店");
		storeName.add("山海关路店");
		storeName.add("人民广场店");
		
		formData.addAll("storeName", storeName);
		formData.add("groupName", "静安区");
		
		Response response = webTarget.request().accept(MediaType.TEXT_PLAIN).put(Entity.form(formData));
		String message = response.readEntity(String.class);
		System.out.println(message);
	}
	
	@Test
	public void testGroupRemove() {
		WebTarget webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/groups/delete");
		List<String> groupNames = new ArrayList<String>();
		
		groupNames.add("徐汇区");
		groupNames.add("崇明区");
		
		Response response = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.json(groupNames));
		int status = response.getStatus();
		System.out.println(status);
	}

}
