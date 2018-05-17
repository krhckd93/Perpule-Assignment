package com.perpule.assignment.sample_webservice.user;

import java.util.ArrayList;

import com.perpule.assignment.sample_webservice.base.BaseDB;
import com.perpule.assignment.sample_webservice.base.DBColumn;

public class UserDB extends BaseDB {
	public UserDB() {
		
	}
	
	protected String getTableName() {
		return "res_user";
	}
	
	public ArrayList<DBColumn> getModelColumns() {
		
		ArrayList<DBColumn> columns = new ArrayList<DBColumn> ();
		columns.add(new DBColumn("id", "SERIAL PRIMARY KEY"));
		columns.add(new DBColumn("first_name", "VARCHAR"));
		columns.add(new DBColumn("last_name", "VARCHAR"));
		columns.add(new DBColumn("username", "VARCHAR"));
		columns.add(new DBColumn("password", "VARCHAR"));
		columns.add(new DBColumn("email", "VARCHAR"));
		columns.add(new DBColumn("phone", "VARCHAR"));
		
		return columns;
	}
}
