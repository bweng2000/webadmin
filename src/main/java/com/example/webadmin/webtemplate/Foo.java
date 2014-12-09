package com.example.webadmin.webtemplate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/foo")
public class Foo {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable get() {
		return new Viewable("/test.html");
	}
}
