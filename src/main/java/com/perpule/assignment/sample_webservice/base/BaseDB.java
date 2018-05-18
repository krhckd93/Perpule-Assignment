package com.perpule.assignment.sample_webservice.base;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.perpule.assignment.sample_webservice.helpers.DatabaseHelper;

@Path("/db/drop_table")
public abstract class BaseDB {
	
	protected abstract String getTableName();
	
	protected abstract ArrayList<DBColumn> getModelColumns();

	public boolean createTable() {
		System.out.print("Table name" + this.getTableName());
		String sql = "CREATE TABLE IF NOT EXISTS ".concat(this.getTableName() + " (" );
		ArrayList<DBColumn> columns = this.getModelColumns();
		for(int i=0; i< columns.size(); i++) {
			if(i != 0) {
				sql = sql.concat(", ");
			}
			sql = sql.concat(columns.get(i).getName() + " " + columns.get(i).getType());
			if(columns.get(i).getName().equalsIgnoreCase("id")) {
				sql.concat("NOT NULL");
			}
			
		}
		sql = sql.concat(" );");
		try {
			DatabaseHelper.getInstance().executeRawUpdate(sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return false;
		
	}
	

	
	@SuppressWarnings("unchecked")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public static String dropTable(@HeaderParam("delete_pass") String password) {
		JSONObject response = new JSONObject();
		try {
			if(password != null) {
				if(!password.equals("SECRETPASSWORD") ) {
					response.put("error", "Incorrect password.");
				} else {
					String sql = "DROP TABLE customer";
					DatabaseHelper.getInstance().executeRawUpdate(sql);	
					sql = "DROP TABLE res_user";
					DatabaseHelper.getInstance().executeRawUpdate(sql);	
				}
			} else {
				response.put("error", "Password required.");
			}
		} catch(Exception e) {
			response.put("error", e.toString());
		}
		return response.toJSONString();
	}
}
