package com.example.musicplayer.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("hello")
public class Hello {
	@GET
	public Response echo() {
		Response r;
		r = Response.ok().entity(new String("Hello Jersey!")).build();
		return r;
	}
	
	@Path("person")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response echoJSON() {
		Response r;
		List<Integer> list = new ArrayList<Integer>();
		list.add(10);
		list.add(30);
		list.add(50);
		r = Response.ok().entity(list).build();
		return r;
	}
}
