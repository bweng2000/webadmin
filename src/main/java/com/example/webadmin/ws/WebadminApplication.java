package com.example.webadmin.ws;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;


import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;


/**
 * This is the simple way to define the subclass of the Application class
 */
/*public class WebadminApplication extends Application {
	
	public Set<Class<?>> getClasses() {
        final Set<Class<?>> resources = new HashSet<Class<?>>();

     // Add my resources.
        resources.add(AdminService.class);
        
        // Add additional features such as support for Multipart.
        resources.add(MultiPartFeature.class);

        return resources;
    }

}*/


/**
 * This is a standard way to define the Application class for JAX-RS
 * Register all necessary classes for Jersey.
 * Import packages for Jersey resource classes.
 * @author bweng
 *
 */
public class WebadminApplication extends ResourceConfig {
	
	public WebadminApplication() {
		packages("com.example.webadmin.ws")
		.register(JacksonFeature.class)		//This step is very important to register Jackson JSON support for Jersey.
		.register(MultiPartFeature.class);	//This step is required to enable MULTIPART_FORM_DATA
    }

}
