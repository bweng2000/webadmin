package webadmin;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.example.dao.FileSystem;
import com.example.webadmin.dao.PlaylistDao;
import com.example.webadmin.dao.PlaylistFSDao;

public class PlaylistDaoTest {

	@Test
	public void testDao() {
		String activeDir = "/home/bweng/tmp/Music/active";
		String inactiveDir = "/home/bweng/tmp/Music/inactive";
		FileSystem activeFS = new FileSystem(activeDir);
		FileSystem inactiveFS = new FileSystem(inactiveDir);
		PlaylistDao dao = new PlaylistFSDao(activeFS, inactiveFS);
		List<String> al = dao.getActiveFileNames();
		List<String> il = dao.getInactiveFileNames();
		System.out.println(Arrays.toString(al.toArray()));
		System.out.println(Arrays.toString(il.toArray()));
	}

}
