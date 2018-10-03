package com.abcplusd.sample.model;

import java.util.Date;

public class IdName {
	private Long id;
	private String name;
	private String description;
	private Date addDate;
	
	public IdName(Long id, Date addDate) {
		this.id = id;
		this.addDate = addDate;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getAddDate() {
		return addDate;
	}
	
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}
	
	@Override
	public String toString() {
		return "idName {id: " + id + " Date: " + addDate +" }";
	}
	
}
