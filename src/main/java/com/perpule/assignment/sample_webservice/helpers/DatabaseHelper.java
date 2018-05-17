package com.perpule.assignment.sample_webservice.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.perpule.assignment.sample_webservice.base.DBColumn;
import com.perpule.assignment.sample_webservice.customer.CustomerDB;
import com.perpule.assignment.sample_webservice.user.UserDB;

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
	
	public void InitializeDB() {
		new UserDB().createTable();
		new CustomerDB().createTable();
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
	
	public int executeRawUpdate(String sql) throws Exception {
		DatabaseHelper dbHelper = DatabaseHelper.getInstance();
		Connection connection = dbHelper.isConnected();
		if(connection != null) {
			Statement stmt = connection.createStatement();
			int update = stmt.executeUpdate(sql);
			connection.close();
			return update;
		} else {
			throw new Exception();
		}
	}
	
	public JSONArray executeRawQuery(String sql) throws Exception {
		DatabaseHelper dbHelper = DatabaseHelper.getInstance();
		Connection connection = dbHelper.isConnected();
		if(connection != null) {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			connection.close();
			return ResultSetToJSON(rs);
		} else {
			throw new Exception();
		}
	}
	
	public JSONObject create() {
		return new JSONObject();
	}
	
	public JSONArray read(String sql) throws Exception {
		DatabaseHelper dbHelper = DatabaseHelper.getInstance();
		Connection connection = dbHelper.isConnected();
		if(connection != null) {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			connection.close();
			return ResultSetToJSON(rs);
		} else {
			throw new Exception();
		}		
	}
	
	public Integer update(String table_name, JSONObject params, String where) throws Exception {
		String sql = "UPDATE " + table_name + " SET ";
		String name_value;
		int i=0;
		for(Object key: params.keySet()) {
			String key_name = (String) key;
			name_value = "";
			if(i!=0) {
				name_value = ", ";
			}
			if(params.get(key_name) instanceof Integer || params.get(key_name) instanceof Boolean) {
				name_value = name_value.concat(key_name + "=" + params.get(key_name)); 
			} else if (params.get(key_name) instanceof String) {
				name_value = name_value.concat(key_name + "='" + params.get(key_name) + "'");
			} else {
				continue;
			}
			sql = sql.concat(name_value);
		}
		
		sql = sql.concat(" " + where + ";");
		
		DatabaseHelper dbHelper = DatabaseHelper.getInstance();
		Connection connection = dbHelper.isConnected();
		if(connection != null) {
			Statement stmt = connection.createStatement();
			int update = stmt.executeUpdate(sql);
			connection.close();
			return update;
		} else {
			throw new Exception();
		}
	}
	
	public int delete( String table_name, String where) throws Exception{
		String sql="";
		sql = sql.concat(" " + where + ";");
		
		DatabaseHelper dbHelper = DatabaseHelper.getInstance();
		Connection connection = dbHelper.isConnected();
		if(connection != null) {
			Statement stmt = connection.createStatement();
			int update = stmt.executeUpdate(sql);
			connection.close();
			return update;
		} else {
			throw new Exception();
		}
	}
	
	
	
	public JSONArray ResultSetToJSON(ResultSet rs) {
		JSONArray array = new JSONArray();

		JSONObject obj;
		try {
			while(rs.next()) {
				obj = new JSONObject();
				try {
					if(rs.getString("id") != null) {
						obj.put("id", rs.getString("id"));	
					}
					if(rs.getString("first_name") != null) {
						obj.put("first_name", rs.getString("first_name"));	
					}
					if(rs.getString("last_name") != null) {
						obj.put("last_name", rs.getString("last_name"));	
					}
					if(rs.getString("username") != null) {
						obj.put("username", rs.getString("username"));	
					}
					if(rs.getString("password") != null) {
						obj.put("password", rs.getString("password"));	
					}
					if(rs.getString("phone") != null) {
						obj.put("phone", rs.getString("phone"));	
					}
					if(rs.getString("email") != null) {
						obj.put("email", rs.getString("email"));	
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
	
	
			   array.add(obj);
			} 
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return array;
	}
	
}
