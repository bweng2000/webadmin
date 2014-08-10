package com.example.webadmin.test;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.example.model.Music;

public class AdminServiceJerseyClientTest {
	public static void main(String[] args) {
		
		//Here we need to register Jackson JSON POJO mapping feature for Jersey client
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		
		WebTarget webTarget = client.target("http://localhost:8080/webadmin/adminrest/home/playlist/update/done");
 
		//note that the timestamp field must use the UTC time zone offset +/-hh:mm to indicate the relative time zone offset to UTC
		String input = "[{\"name\":\"notice-2.mp3\", \"status\":1, \"special\":true, \"playtime\":\"2014-05-23T012:30:00-04:00\"},"
				+ "{\"name\":\"hello.mp3\", \"status\":0, \"special\":false, \"playtime\":null}]";
 
		Response response = webTarget.request("application/json").accept(MediaType.APPLICATION_JSON).post(Entity.json(input));
 
		System.out.println("Output from Server .... \n");
		GenericType<List<Music>> musicListType = new GenericType<List<Music>>() {};
		List<Music> output = response.readEntity(musicListType);
		System.out.println(Arrays.toString(output.toArray()));
	}

}
