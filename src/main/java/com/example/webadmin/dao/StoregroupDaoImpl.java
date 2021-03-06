package com.example.webadmin.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.example.musicplayer.model.Device;
import com.example.musicplayer.model.Music;
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
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

	@Override
	public List<Group> getAllGroups() {
		String sqlQuery = "SELECT * FROM Groups";
		GroupMapper mapper = new GroupMapper();
		List<Group> allGroups = jdbcTemplate.query(sqlQuery, mapper);
		return allGroups;
	}

	@Override
	public List<Group> getGroupByCategory(Category cat) throws DataAccessException {
		String sqlQuery = "SELECT * FROM Groups WHERE category=?";
		List<Group> groups = jdbcTemplate.query(sqlQuery, new Object[] {cat.toString()}, new GroupMapper());
		return groups;
	}

	@Override
	public Group getGroupById(int groupID) {
		String query = "SELECT * FROM Groups WHERE id=?";
		Group group = jdbcTemplate.queryForObject(query, new Object[] {groupID}, new GroupMapper());
		return group;
	}	
	
	@Override
	public Group getGroupByName(String groupName) {
		String sqlQuery = "SELECT * FROM Groups WHERE name=?";
		Group group = jdbcTemplate.queryForObject(sqlQuery, new Object[] {groupName}, new GroupMapper());
		return group;
	}

	@Override
	public void addStoreToGroup(Store store, Group group) {
		/*List<Group> groups = getGroupsOfStore(store);
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
		String storeID = jdbcTemplate.queryForObject(sqlQuery, new Object[] {storeName}, String.class);
		
		sqlQuery = "SELECT id FROM Groups WHERE name=?";
		String groupName = group.getGroupName();
		int groupID = jdbcTemplate.queryForObject(sqlQuery, new Object[] {groupName}, Integer.class);
		*/
		
		String storeID = store.getStoreID();
		int groupID = group.getId();
		Category groupCategory = group.getCategory();
		
		String sqlQuery = "INSERT INTO StoreGroup (storeID, groupID, groupCategory) VALUES (?, ?, ?)";
		jdbcTemplate.update(sqlQuery, new Object[] {storeID, groupID, groupCategory.toString()});
		
		/*groupSet.add(group);
		store.setGroups(groupSet);
		return true;*/
	}

	@Override
	public void removeStoreFromGroup(Store store, Group group) {
		String storeID = store.getStoreID();
		int groupID = group.getId();
		String sqlQuery = "DELETE FROM StoreGroup WHERE storeID=? AND groupID=?";
		jdbcTemplate.update(sqlQuery, new Object[] {storeID, groupID});
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

			System.out.println(expireDate.getTime());
			System.out.println(System.currentTimeMillis());
			System.out.println(expireDate.getTime()-System.currentTimeMillis());
			
			Runnable task = new Runnable() {
		        @Override
		        public void run() {
		        	System.out.println("Running the scheduled task.");
		        	String query = "DELETE FROM Groups WHERE name=?";
		    		jdbcTemplate.update(query, new Object[] {groupName});
		        }
		    };
			scheduler.schedule(task, expireDate.getTime()-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		    
		    /*try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		    //scheduler.shutdownNow();
		}
		return numRows;
	}
	
	/*private void scheduleDeleteTask(Timestamp expireDate, final String groupName) {
		
		Runnable task = new Runnable() {
	        @Override
	        public void run() {
	        	System.out.println("Running the scheduled task...");
	        	String query = "DELETE FROM Groups WHERE name=?";
	    		jdbcTemplate.update(query, new Object[] {groupName});
	        }
	    };
		scheduler.schedule(task, expireDate.getTime()-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}*/


	@Override
	public void removeGroup(String groupName) {
		String sqlQuery = "DELETE FROM Groups WHERE name=?";
		jdbcTemplate.update(sqlQuery, new Object[] {groupName});
	}
	
	@Override
	public void removeSomeGroups(final List<String> groupNames) {
		String query = "DELETE FROM Groups WHERE name=?";

		jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				String groupName = groupNames.get(i);
				Group group = getGroupByName(groupName);
				ps.setString(1, group.getGroupName());
			}

			@Override
			public int getBatchSize() {
				return groupNames.size();
			}
			
		});
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
		String storeID = store.getStoreID();
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
	public Store getStorebyId(String id) {
		String sqlQuery = "SELECT * FROM Store WHERE storeID=?";
		Store store = jdbcTemplate.queryForObject(sqlQuery, new Object[] {id}, new StoreMapper());
		return store;
	}

	@Override
	public Store addNewStore(String id, String name, long deviceID) {
		Store store = new Store();
		store.setStoreID(id);
		store.setStoreName(name);
		store.setDeviceID(deviceID);
		String sqlQuery = "INSERT INTO Store VALUES (?, ?, ?)";
		jdbcTemplate.update(sqlQuery, new Object[] {id, name, deviceID});
		return store;
	}

	@Override
	public Store getStoreByDeviceID(long deviceID) {
		String query = "SELECT * FROM Store WHERE deviceID=?";
		Store store = jdbcTemplate.queryForObject(query, new Object[] {deviceID}, new StoreMapper());
		return store;
	}
	
	@Override
	public List<Store> getStoreUnassigned() {
		String query = "SELECT * FROM Store WHERE storeID NOT IN (SELECT storeID FROM StoreGroup)";
		List<Store> unassignedStores = jdbcTemplate.query(query, new StoreMapper());
		return unassignedStores;
	}

}
