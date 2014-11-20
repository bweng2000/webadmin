package com.example.musicplayer.model;

import java.sql.Timestamp;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * This is the Music class that represents the music in the playlist
 * @author bweng
 *
 */
public class Music {
	private int id;
	private String name;
	@JsonIgnore
	private String location;
	private boolean status;
	private boolean special;
	private Timestamp playtime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean getSpecial() {
		return special;
	}

	public void setSpecial(boolean special) {
		this.special = special;
	}

	public Timestamp getPlaytime() {
		return playtime;
	}

	public void setPlaytime(Timestamp playtime) {
		this.playtime = playtime;
	}

	@Override
	public String toString() {
		return "Music [id=" + id + ", name=" + name + ", location=" + location
				+ ", status=" + status + ", special=" + special + ", playtime="
				+ playtime + "]";
	}
	
}
