package com.perpule.assignment.sample_webservice.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import com.perpule.assignment.sample_webservice.base.DBColumn;
import com.perpule.assignment.sample_webservice.customer.CustomerDB;

@SuppressWarnings("unchecked")
public class DatabaseHelper {

	public static DatabaseHelper instance = null;
	public static Connection connection = null;
	private String connectionURL = "jdbc:postgresql://35.200.242.186/postgres";
	private String username = "postgres";
	private String password =  "password";
	
	private DatabaseHelper () {
		
	}
	
	
	public static DatabaseHelper getInstance() {
		if(instance == null) {
			instance = new DatabaseHelper();
		}
		return instance;
	}
	
	public Connection isConnected() {
		try {
		if(connection == null) {
			connection = getConnection();
		}
		if(connection != null && connection.isValid(10000)) {
			return connection;
		} else {
			return getConnection();
		}
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}	
	}
	
	private Connection getConnection() {
		try {
	    	connection = null;
	    	Class.forName("org.postgresql.Driver").newInstance();
	    	connection = DriverManager.getConnection(connectionURL, username, password);
	    	System.out.println("Database Connection");
	    	System.out.println(connection.isValid(1000));
	    	if(connection.isValid(1000)) {
	    		System.out.println("Connection established");
	    		return connection;
	    	}
	    	return null;
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
	}
	
	public JSONObject execute(String sql) {
		JSONObject response = new JSONObject();
		try {
			DatabaseHelper dbHelper = DatabaseHelper.getInstance();
			Connection connection = dbHelper.isConnected();
			if(connection != null) {
				Statement stmt = connection.createStatement();
//				int a = stmt.executeUpdate(sql);
				int update = stmt.executeUpdate(sql);
				 ResultSet rs = stmt.getGeneratedKeys();
				 /*if (rs != null && rs.next()) {
				  Long key = rs.getLong(1);
				 }*/
				response.put("result", update);
				response.put("result_set", ResultSetToJSON(rs));
				connection.close();
				return response;
			} else {
				response.put("error", "Failed to connect to database.");
				return response;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			response.put("error", ex.toString());
			if(connection != null) {
				try {
				connection.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}
	}
	
	public JSONObject create() {

		return new JSONObject();
	}
	
	public JSONObject read(String sql) throws Exception {
		JSONObject response = new JSONObject();
		try {
			DatabaseHelper dbHelper = DatabaseHelper.getInstance();
			Connection connection = dbHelper.isConnected();
			if(connection != null) {
				Statement stmt = connection.createStatement();
//				int a = stmt.executeUpdate(sql);
				 ResultSet rs = stmt.executeQuery(sql);
				 /*if (rs != null && rs.next()) {
				  Long key = rs.getLong(1);
				 }*/
				response.put("result", ResultSetToJSON(rs));
				connection.close();
				return response;
			} else {
				response.put("error", "Failed to connect to database.");
				return response;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			response.put("error", ex.toString());
			if(connection != null) {
				try {
				connection.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}
	}
	
	public JSONObject update() {
		
		return new JSONObject();
	}
	
	public int delete() {
		
		return -1;
	}
	
	public boolean createTable() {
		
		String sql = "CREATE TABLE IF NOT EXISTS customer (";
		ArrayList<DBColumn> columns = new CustomerDB().getModelColumns();
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
			Connection connection = getConnection();
			if(connection.isValid(1000)) {
				
				Statement statement = connection.createStatement();
				statement.executeUpdate(sql);
				connection.close();
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if(connection != null) {
				try {
				connection.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	public JSONArray ResultSetToJSON(ResultSet rs) {
		JSONArray array = new JSONArray();

		JSONObject obj;
		try {
			while(rs.next()) {
				obj = new JSONObject();
				try {
				   obj.put("first_name", rs.getString("first_name"));
				   obj.put("last_name", rs.getString("last_name"));
				   obj.put("username", rs.getString("username"));
				   obj.put("password", rs.getString("password"));
				} catch(Exception ex) {
					ex.printStackTrace();
				}
	
	
			   array.put(obj);
			} 
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return array;
	}
	
}
