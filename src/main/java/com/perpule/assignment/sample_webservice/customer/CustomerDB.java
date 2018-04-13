package com.perpule.assignment.sample_webservice.customer;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.perpule.assignment.sample_webservice.base.DBColumn;

public class CustomerDB {
	
	public CustomerDB() {
		
	}
	
	public ArrayList<DBColumn> getModelColumns() {
		
		ArrayList<DBColumn> columns = new ArrayList<DBColumn> ();
		columns.add(new DBColumn("id", "SERIAL PRIMARY KEY"));
		columns.add(new DBColumn("first_name", "VARCHAR"));
		columns.add(new DBColumn("last_name", "VARCHAR"));
		columns.add(new DBColumn("username", "VARCHAR"));
		columns.add(new DBColumn("password", "VARCHAR"));
		
		return columns;
	}
}
