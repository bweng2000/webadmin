package com.example.webadmin.dao;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.example.webadmin.model.Group;
import com.example.webadmin.model.Group.Category;
import com.example.webadmin.model.Store;

/**
 * This is the DAO implementing class that interface with the DB for Store Group management
 * @author bweng
 *
 */
public class StoregroupDaoImpl implements StoregroupDao {
	
	@Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Group> getAllGroups() {
		String sqlQuery = "SELECT * FROM Groups";
		GroupMapper mapper = new GroupMapper();
		List<Group> allGroups = jdbcTemplate.query(sqlQuery, mapper);
		return allGroups;
	}

	@Override
	public List<Group> getGroupByCategory(Category cat) {
		String sqlQuery = "SELECT * FROM Groups WHERE category=?";
		List<Group> groups = jdbcTemplate.query(sqlQuery, new Object[] {cat.toString()}, new GroupMapper());
		return groups;
	}

	@Override
	public Group getGroupByName(String groupName) {
		String sqlQuery = "SELECT * FROM Groups WHERE name=?";
		Group group = jdbcTemplate.queryForObject(sqlQuery, new Object[] {groupName}, new GroupMapper());
		return group;
	}

	@Override
	@Transactional
	public boolean addStoreToGroup(Store store, Group group) {
		List<Group> groups = getGroupsOfStore(store);
		Set<Group> groupSet = new HashSet<Group>(groups);
		store.setGroups(groupSet);
		
		if(groupSet.contains(store))
			return false;
		
		for(Group g : groupSet) {
			//check if this store is already in one of the group categories. If so stop. No store can be in one group category more than once.
			if(g.getCategory() == group.getCategory())
				return false;
		}
		String sqlQuery = "SELECT storeID FROM Store WHERE storeName=?";
		String storeName = store.getStoreName();
		int storeID = jdbcTemplate.queryForObject(sqlQuery, new Object[] {storeName}, Integer.class);
		
		sqlQuery = "SELECT id FROM Groups WHERE name=?";
		String groupName = group.getGroupName();
		int groupID = jdbcTemplate.queryForObject(sqlQuery, new Object[] {groupName}, Integer.class);
		
		sqlQuery = "INSERT INTO StoreGroup VALUES (?, ?)";
		jdbcTemplate.update(sqlQuery, new Object[] {storeID, groupID}, new int[] {Types.INTEGER, Types.INTEGER});
		
		groupSet.add(group);
		store.setGroups(groupSet);
		return true;
	}

	@Override
	public boolean removeStoreFromGroup(Store store, Group group) {
		int storeID = store.getStoreID();
		int groupID = group.getId();
		String sqlQuery = "DELETE FROM StoreGroup WHERE storeID=? AND groupID=?";
		int numRow = jdbcTemplate.update(sqlQuery, new Object[] {storeID, groupID});
		return numRow > 0 ? true : false;
	}

	@Override
	public int addNewGroup(final String groupName, Category category,
			Timestamp expireDate) {
		if (category == Category.临时 && expireDate == null) {
			throw new IllegalArgumentException();
		}
		String sqlQuery = "INSERT INTO Groups (name, category, expireDate) VALUES (?, ?, ?)";
		int numRows = jdbcTemplate.update(sqlQuery, new Object[] { groupName,
				category.toString(), expireDate });
		//schedule the deleting temporary group task
		if (category == Category.临时) {
			/*Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					removeGroup(groupName);
				}
			}, new Date(expireDate.getTime()));*/
			final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			Runnable task = new Runnable() {
		        @Override
		        public void run() {
		        	//removeGroup(groupName);
		        	String query = "DELETE FROM Groups WHERE name=?";
		    		jdbcTemplate.update(query, new Object[] {groupName});
		        }
		    };
		    scheduler.schedule(task, expireDate.getTime()-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		    try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    scheduler.shutdownNow();
		}
		return numRows;
	}


	@Override
	public void removeGroup(String groupName) {
		String sqlQuery = "DELETE FROM Groups WHERE name=?";
		jdbcTemplate.update(sqlQuery, new Object[] {groupName});
	}

	@Override
	public List<Store> getStoresFromGroup(Group group) {
		int groupID = group.getId();
		String sqlQuery = "SELECT * FROM Store INNER JOIN StoreGroup ON Store.storeID=StoreGroup.storeID AND groupID=? ORDER BY Store.storeID";
		List<Store> stores = jdbcTemplate.query(sqlQuery, new Object[] {groupID}, new StoreMapper());
		return stores;
	}

	@Override
	public List<Group> getGroupsOfStore(Store store) {
		int storeID = store.getStoreID();
		String sqlQuery = "SELECT * FROM Groups g INNER JOIN StoreGroup sg ON g.id=sg.groupID AND sg.storeID=? ORDER BY g.category";
		List<Group> groups = jdbcTemplate.query(sqlQuery, new Object[] {storeID}, new GroupMapper()); 
		return groups;
	}

	@Override
	public Store getStoreByName(String name) {
		String sqlQuery = "SELECT * FROM Store WHERE storeName=?";
		Store store = jdbcTemplate.queryForObject(sqlQuery, new Object[] {name}, new StoreMapper());
		return store;
	}

	@Override
	public Store getStorebyId(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
