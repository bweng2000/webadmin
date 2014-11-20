package com.example.webadmin.test;

import static org.junit.Assert.*;

import java.util.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AdminServiceTest {
	
	private Client client;
	
	@Before
	public void init() {
		//Here we need to register Jackson JSON POJO mapping feature for Jersey client
		client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
	}

	//@Ignore
	@Test
	public void testMusicGroupAdd() {
		String groupName = "静安区";
		WebTarget webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/playlist/group/" + groupName + "/add");
		List<String> musics = new ArrayList<String>();
		musics.add("好天气.mp3");
		musics.add("山居岁月.mp3");
		musics.add("symphony.mp3");
		
		Response response = webTarget.request().put(Entity.json(musics));
		String message = response.readEntity(String.class);
		System.out.println(message);
	}
	
	//@Ignore
	@Test
	public void testMusicGroupRemove() {
		String groupName = "静安区";
		WebTarget webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/playlist/group/" + groupName + "/remove");
		List<String> musics = new ArrayList<String>();
		musics.add("好天气.mp3");
		musics.add("山居岁月.mp3");
		musics.add("symphony.mp3");
		
		Response response = webTarget.request().put(Entity.json(musics));
		String message = response.readEntity(String.class);
		System.out.println(message);
	}

}
