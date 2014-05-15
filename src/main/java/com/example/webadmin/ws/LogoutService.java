package com.example.webadmin.ws;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

@Path("/logout")
public class LogoutService {

	@GET
	public Response userLogout(@Context HttpServletRequest request) {
		Response r;
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.logout();
		String contextRoot = request.getContextPath();
		String homepage = contextRoot + "/pages/login.html";
		URI uri;
		try {
			uri = new URI(homepage);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		r = Response.temporaryRedirect(uri).build();
		return r;
	}

}
