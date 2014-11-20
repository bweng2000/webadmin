package com.example.musicplayer.ws;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.musicplayer.model.AndroidSoftwareVersion;



@Path("/android")
public class AndroidUpdate {
	
	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAndroidVersion() {
		File xmlFile = new File(getClass().getClassLoader().getResource("android-version.xml").getFile());
		Document dom;
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        AndroidSoftwareVersion version = new AndroidSoftwareVersion();
        
		try {
			// use the factory to take an instance of the document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using the builder to get the DOM mapping of the
			// XML file
			dom = db.parse(xmlFile);

			Element doc = dom.getDocumentElement();
			
			String versionCode = null, versionName = null;
		    NodeList nl;
		    nl = doc.getElementsByTagName("code");
		    if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
		        versionCode = nl.item(0).getFirstChild().getNodeValue();
		    }
		    nl = doc.getElementsByTagName("name");
		    if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
		        versionName = nl.item(0).getFirstChild().getNodeValue();
		    }
		    version.setVersionCode(Integer.parseInt(versionCode));
		    version.setVersionName(versionName);

		} catch (ParserConfigurationException pce) {
			System.out.println(pce.getMessage());
		} catch (SAXException se) {
			System.out.println(se.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		
		return Response.ok(version).build();
	}

	@Path("/software")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadApk() {
		String apkFileName = "musicplayer.apk";
		File file = new File(getClass().getClassLoader()
				.getResource(apkFileName).getFile());

		return Response
				.ok(file, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition",
						"attachment; filename=" + apkFileName).build();
	}
}
