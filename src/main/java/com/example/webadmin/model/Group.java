package com.example.webadmin.model;

import java.sql.Timestamp;

public class Group implements Comparable<Group> {
	/*public enum Category {
		AREA("区域"),
		LEVEL("级别"),
		TEMPORARY("临时");
		
		private final String categoryName;
		Category(String categoryName) {
			this.categoryName = categoryName;
		}
	}*/
	
	public enum Category {
		区域, 级别, 临时;
	}
	
	private int id;
	private String groupName;
	private Category category;
	private Timestamp expireDate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Timestamp getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Timestamp expireDate) {
		this.expireDate = expireDate;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{id: ").append(id).append(", ");
		sb.append("groupName: ").append(groupName).append(", ");
		sb.append("category: ").append(category.name()).append(", ");
		String expireDateString = expireDate != null ? expireDate.toString() : "null";
		sb.append("expireDate: ").append(expireDateString).append("}");
		return sb.toString();
	}
	
	/**
	 * This method implements the Comparable interface.
	 * This Group priority is based on group category.
	 * 临时 > 级别 > 区域
	 */
	@Override
	public int compareTo(Group o) {
		if(this.category == o.category)
			return 0;
		
		if (this.category == Category.临时) {
			return 1;
		} else if (this.category == Category.级别) {
			if (o.category == Category.临时)
				return -1;
			else
				return 1;
		} else {
			return -1;
		}
	}
	
}
