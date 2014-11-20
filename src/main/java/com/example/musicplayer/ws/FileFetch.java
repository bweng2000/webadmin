package com.example.musicplayer.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.example.musicplayer.dao.FileSystem;
import com.example.musicplayer.model.Music;
import com.example.webadmin.dao.PlaylistDao;
import com.example.webadmin.dao.StoregroupDao;
import com.example.webadmin.model.Group;
import com.example.webadmin.model.Store;

@Component
@Path("filelist")
public class FileFetch {
	
	//private static final String HOME = System.getProperty("user.home");	//home directory on UNIX/Linux
	//private static final String FILE_PATH = HOME + "/Music/";
	//private final FileSystem fs = new FileSystem(FILE_PATH);
	
	@Autowired
	private PlaylistDao pldao;
	@Autowired
	private StoregroupDao sgdao;
	
	/*public FileFetch(@Context HttpServletRequest request) {
		WebApplicationContext context = WebApplicationContextUtils.
				getRequiredWebApplicationContext(request.getSession().getServletContext());
		dao = (PlaylistDAO) context.getBean(PlaylistDAO.class);
	}*/
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response getFileList(@QueryParam("deviceID") long deviceID) {
		List<Group> groups;
		try {
			Store store = sgdao.getStoreByDeviceID(deviceID);
			groups = sgdao.getGroupsOfStore(store);
		} catch (DataAccessException ex) {
			return Response.status(Status.NOT_FOUND).build();
		}
		PriorityQueue<Group> groupQueue = new PriorityQueue<Group>(10, Collections.reverseOrder());
		for(Group g : groups) {
			groupQueue.add(g);
		}
		Group currentGroup = groupQueue.poll();
		if(currentGroup == null)
			return Response.ok(null).build();
		
		List<Music> playlist = pldao.getAllMusicFromGroup(currentGroup);
		String headerValue = "application/json; charset=utf-8";
		return Response.ok(playlist).header("Content-Type", headerValue).build();
	}
	
	@Path("{fileID}")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("fileID") int fileID) {
		try {
			File file = pldao.getFileById(fileID);
			String headerValue = "attachment; filename=" + Integer.toString(fileID) + ".mp3";
			return Response.ok(file).header("Content-Disposition", headerValue).build();
		} catch(DataAccessException | FileNotFoundException e) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
	}

	/*@Path("allinone")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public StreamingOutput getAllFile(@QueryParam("fnames") String filenames) {
		String[] names = filenames.split(",");
		final File[] files = fs.getFilesByNames(names);
	
		return new StreamingOutput() {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {

				for(File f : files) {
					FileInputStream fstream = new FileInputStream(f);
					try {
						byte[] buf = new byte[1024];
						while(fstream.read(buf, 0, buf.length) != -1) {
							output.write(buf, 0, buf.length);
						}
					} catch (IOException e) {
						throw new WebApplicationException(Status.NOT_FOUND);
					} finally {
						fstream.close();
					}
				}
			
			}
		};
	}*/
	
}
