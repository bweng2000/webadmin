package com.example.webadmin.ws;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.musicplayer.model.Music;
import com.example.webadmin.dao.PlaylistDao;
import com.example.webadmin.dao.StoregroupDao;
import com.example.webadmin.model.Group;

/**
 * This is the web service class that allows web admin to do CRUD operations.
 * @author bweng
 *
 */
@Component
@Path("/home")
public class AdminService {
	
	//!This may be a problem in Windows since Windows 7 cannot resolve user.home system property!
	private static final String HOME = System.getProperty("user.home");	//home directory on UNIX/Linux
	
	private static final String SERVER_UPLOAD_LOCATION = HOME + "/Music/";
	private static final String headerValue = "application/json; charset=utf-8";
	
	@Autowired
	private PlaylistDao playlistDao;
	@Autowired
	private StoregroupDao storegroupDao;
	
	@GET
	public Response test() {
		return Response.ok("Hello Dude!").build();
	}
	
	/* This Jersey MVC part is not working for Jersey 2.7 when using /adminrest/hello/aloha as the path name
	@Path("/aloha")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable hello() {
		return new Viewable("/index.jsp");
	}*/
	
	@Path("/echo")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response echo(@FormParam("username") String username, @FormParam("password") String password) {
		String[] token = new String[] {username, password};
		List<String> credential = new ArrayList<String>();
		credential.addAll(Arrays.asList(token));
		return Response.ok(credential).build(); 
	}
	
	@Path("/playlist")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllMusic() {
		List<Music> playlist = playlistDao.getAllMusic();
		return Response.ok(playlist).header("Content-Type", headerValue).build();
	}
	
	@Path("/playlist/update")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response finishUpdate(List<Music> updateList) {
		List<Music> playlist;
		try {
			playlistDao.updateList(updateList);
			playlist = playlistDao.getAllMusic();
		} catch (DataAccessException ex) {
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST).entity(ex.getMessage()).build());
		}
		
		return Response.ok(playlist).header("Content-Type", headerValue).build();
	}
	
	@Path("/edit/upload")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Transactional
	public Response uploadFile(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {
		
		String fileName = contentDispositionHeader.getFileName();
		String utf8FileName = null;
		try {
			utf8FileName = new String(fileName.getBytes("iso-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		File musicDir = new File(SERVER_UPLOAD_LOCATION);
		if(!musicDir.exists()) {
			musicDir.mkdir();
		}
		String filePath = SERVER_UPLOAD_LOCATION + utf8FileName;
		
		List<Music> updatedList;
		// save the file to the server
		try {
			saveFile(fileInputStream, filePath);
			Music newItem = new Music();
			newItem.setName(utf8FileName);
			newItem.setLocation(SERVER_UPLOAD_LOCATION);
			playlistDao.addNewMusicForStaging(newItem);
			updatedList = playlistDao.getAllMusic();
		} catch (IOException e) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build());
		} catch (DataAccessException dae) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(dae.getMessage()).build());
		}

		return Response.status(200).entity(updatedList).header("Content-Type", headerValue).build();
	}

	// save uploaded file to a defined location on the server
	private void saveFile(InputStream uploadedInputStream, String serverLocation)
			throws IOException {

		OutputStream outpuStream = new FileOutputStream(
				new File(serverLocation));
		int read = 0;
		byte[] bytes = new byte[1024];
		outpuStream = new FileOutputStream(new File(serverLocation));

		while ((read = uploadedInputStream.read(bytes)) != -1) {
			outpuStream.write(bytes, 0, read);
		}
		outpuStream.flush();
		outpuStream.close();
	}

	@Path("playlist/group/{groupName}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response getMusicsByGroup(@PathParam("groupName") String groupName) {
		List<Music> groupMusics;
		try {
			Group group = storegroupDao.getGroupByName(groupName);
			groupMusics = playlistDao.getAllMusicFromGroup(group);
		} catch (DataAccessException e) {
			throw new WebApplicationException(Response.status(Status.NOT_FOUND).build());
		}
		return Response.ok().entity(groupMusics).header("Content-Type", headerValue).build();
	}
	
	@Path("playlist/group/{groupName}/add")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response addMusicToGroup(List<String> musicNames, @PathParam("groupName") String groupName) {
		Response r = null;
		Group group = storegroupDao.getGroupByName(groupName);
		List<Music> musics = playlistDao.getMusicsFromNames(musicNames);
		boolean[] flags = playlistDao.addAllMusicToGroup(musics, group);
		StringBuilder errMsg = new StringBuilder();
		for(int i=0; i<musics.size(); i++) {
			if(flags[i] == false) {
				errMsg.append(musics.get(i).getName()).append("不能加到该组中").append("\n");
			}
		}
		if(errMsg.length() == 0) {
			r = Response.ok("添加成功").build();
		} else {
			r = Response.status(Status.BAD_REQUEST).entity(errMsg).header("Content-Type", headerValue).build();
		}
		return r;
	}
	
	@Path("playlist/group/{groupName}/remove")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response removeMusicFromGroup(List<String> musicNames, @PathParam("groupName") String groupName) {
		Response r = null;
		Group group = storegroupDao.getGroupByName(groupName);
		List<Music> musics = playlistDao.getMusicsFromNames(musicNames);
		boolean[] flags = playlistDao.removeAllMusicFromGroup(musics, group);
		StringBuilder errMsg = new StringBuilder();
		for(int i=0; i<musics.size(); i++) {
			if(flags[i] == false) {
				errMsg.append(musics.get(i).getName()).append("不能从该组删除").append("\n");
			}
		}
		if(errMsg.length() == 0) {
			r = Response.ok("删除成功").build();
		} else {
			r = Response.status(Status.BAD_REQUEST).entity(errMsg).header("Content-Type", headerValue).build();
		}
		return r;
	}
}
