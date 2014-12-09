package com.example.webadmin.webtemplate;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.MvcFeature;

/**
 * This is the customized application class for web templates.
 * @author bweng
 *
 */
public class WebTemplateApplication extends ResourceConfig {
	public WebTemplateApplication() {
		packages("com.example.webadmin.webtemplate")
		.register(MvcFeature.class);
		//.property(MvcFeature.TEMPLATE_BASE_PATH, "/");
    }
}
