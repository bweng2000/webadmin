package com.example.webadmin.test;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.webadmin.dao.StoregroupDao;
import com.example.webadmin.dao.StoregroupDaoImpl;
import com.example.webadmin.model.Group;
import com.example.webadmin.model.Group.Category;
import com.example.webadmin.model.Store;

public class StoregroupDaoTest {

	private StoregroupDao dao;
	
	@Before
	public void setup() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		dao = context.getBean(StoregroupDao.class);
	}
	
	@Ignore
	@Test
	public void testGroup() {
		List<Group> groups = dao.getAllGroups();
		for (Group g : groups) {
			switch (g.getId()) {
			case 1000:
				assertEquals("长宁区", g.getGroupName());
				assertEquals(Category.区域, g.getCategory());
				break;
			case 1001:
				assertEquals("旗舰店", g.getGroupName());
				assertEquals(Category.级别, g.getCategory());
				break;
			}
		}
	}

	
	@Ignore
	@Test
	public void testQueryGroup() {
		String categoryName = "区域";
		Group.Category category = Group.Category.valueOf(categoryName);
		List<Group> groups = dao.getGroupByCategory(category);
		Group group = groups.get(0);
		assertEquals("长宁区", group.getGroupName());
		assertEquals(1000, group.getId());
		
		group = groups.get(1);
		List<Store> stores = dao.getStoresFromGroup(group);
		assertEquals("长宁店", stores.get(0).getStoreName());
		assertEquals("浦东一号店", stores.get(1).getStoreName());
		
	}
	
	@Ignore
	@Test
	public void testQueryStore() {
		String name = "长宁店";
		Store store = dao.getStoreByName(name);
		List<Group> groups = dao.getGroupsOfStore(store);
		
		assertEquals("长宁区", groups.get(0).getGroupName());
		assertEquals("旗舰店", groups.get(1).getGroupName());
	}
	
	@Ignore
	@Test
	public void testAddStore() {
		Store store = dao.getStoreByName("长宁店");
		Group group = dao.getGroupByName("促销组");
		//assertTrue(dao.addStoreToGroup(store, group));
		group = dao.getGroupByName("静安区");
		//assertFalse(dao.addStoreToGroup(store, group));
	}
	
	//@Ignore
	@Test
	public void testAdd() {
		String groupName = "测试组-8";
		Category category = Category.临时;
		//Timestamp expireTime = new Timestamp(1406253589000L);
		@SuppressWarnings("deprecation")
		Timestamp expireTime = new Timestamp(114, 8, 4, 10, 39, 0, 0);
		int numRows = dao.addNewGroup(groupName, category, expireTime);
		/*assertEquals(1, numRows);
		Group group = dao.getGroupByName(groupName);
		assertEquals("徐汇区", group.getGroupName());
		assertEquals(Category.区域, group.getCategory());*/
	}
	
	@Ignore
	@Test
	public void testDelete() {
		Store store = dao.getStoreByName("浦东一号店");
		Group group = dao.getGroupByName("打折组");
		//assertTrue(dao.removeStoreFromGroup(store, group););
	}

}
