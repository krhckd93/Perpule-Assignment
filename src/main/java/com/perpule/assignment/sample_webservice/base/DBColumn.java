package com.perpule.assignment.sample_webservice.base;

public class DBColumn {

	public String name;
	public String type;

	
	public DBColumn(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
}
