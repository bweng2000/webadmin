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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.example.model.Music;
import com.example.webadmin.dao.PlaylistDao;

/**
 * This is the web service class that allows web admin to do CRUD operations.
 * @author bweng
 *
 */
@Path("/home")
public class AdminService {
	
	private static final String HOME = System.getProperty("user.home");	//home directory on UNIX/Linux
	private static final String SERVER_UPLOAD_LOCATION = HOME + "/Music/";
	
	@Autowired
	private PlaylistDao playlistDao;
	
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
		List<Music> playlist = playlistDao.getAllList();
		String headerValue = "application/json; charset=utf-8";
		return Response.ok(playlist).header("Content-Type", headerValue).build();
	}
	
	@Path("/playlist/update/done")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response finishUpdate(List<Music> updateList) {
		List<Music> addedItems = new ArrayList<Music>();
		List<Music> removedItems = new ArrayList<Music>();
		for(Music music : updateList) {
			if(music.getStatus() == true)
				addedItems.add(music);
			else
				removedItems.add(music);
		}
		
		playlistDao.removeFromList(removedItems);
		playlistDao.addToList(addedItems);
		//playlistDao.removeFromList(removedItems);
		List<Music> playlist = playlistDao.getAllList();
		
		String headerValue = "application/json; charset=utf-8";
		return Response.ok(playlist).header("Content-Type", headerValue).build();
	}
	
	@Path("/edit/upload")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
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
		
		String filePath = SERVER_UPLOAD_LOCATION + utf8FileName;
		// save the file to the server
		saveFile(fileInputStream, filePath);

		String output = "File saved to server location : " + filePath;
		return Response.status(200).entity(output).build();
	}

	// save uploaded file to a defined location on the server
	private void saveFile(InputStream uploadedInputStream, String serverLocation) {
		try {
			OutputStream outpuStream = new FileOutputStream(new File(
					serverLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			outpuStream = new FileOutputStream(new File(serverLocation));

			while ((read = uploadedInputStream.read(bytes)) != -1) {
				outpuStream.write(bytes, 0, read);
			}
			outpuStream.flush();
			outpuStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
