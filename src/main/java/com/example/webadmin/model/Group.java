package com.example.webadmin.model;

import java.sql.Timestamp;

public class Group {
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
	
}
