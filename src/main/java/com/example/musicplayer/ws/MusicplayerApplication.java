package com.example.musicplayer.ws;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.example.webadmin.ws.JacksonConfigurator;

public class MusicplayerApplication extends ResourceConfig {
	public MusicplayerApplication() {
		packages("com.example.musicplayer.ws")
		.register(JacksonFeature.class)		//This step is very important to register Jackson JSON support for Jersey.
		.register(JacksonConfigurator.class);
	}
}
