package com.example.webadmin.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

@Path("/login")
public class LoginService {
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	//@Produces(MediaType.APPLICATION_JSON)
	public Response userAuthentication(@FormParam("username") String username,
			@FormParam("password") String password) {

		Response r = null;
		String errMsg;

		Factory<SecurityManager> factory = new IniSecurityManagerFactory(
				"classpath:shiro.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);

		UsernamePasswordToken token = new UsernamePasswordToken(username,
				password);
		//token.setRememberMe(true);
		// SecurityUtils.setSecurityManager(sm);
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		// session.setTimeout(10000);

		try {
			currentUser.login(token);
			r = Response.ok().build();
		} catch (AuthenticationException ex) {
			errMsg = "Incorrect username or password!";
			System.out.println(errMsg);
			r = Response.status(401).entity(errMsg).build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(Response
					.status(Response.Status.SERVICE_UNAVAILABLE)
					.entity("Service unavailable").build());
		}

		return r;
	}
}
