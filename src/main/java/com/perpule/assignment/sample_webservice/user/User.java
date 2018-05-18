package com.perpule.assignment.sample_webservice.user;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.perpule.assignment.sample_webservice.helpers.AuthenticationHelper;
import com.perpule.assignment.sample_webservice.helpers.DatabaseHelper;

@Path("/user")
public class User {
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getUser(@DefaultValue("-1") @QueryParam("id") Long user_id, @HeaderParam("auth") String token) {
//		System.out.println(headers.getHeaderString("auth"));
		JSONObject response = new JSONObject();
		boolean authenticated;
		try {
			authenticated = new AuthenticationHelper().authenticate(token);
		} catch(Exception ex) {
			ex.printStackTrace();
			response.put("error", ex.toString());
			return response.toJSONString();
		}
		if(authenticated) {

			String sql = ""; 
			if(user_id != -1 ) {
				sql = "SELECT id, first_name, last_name, username, password, email, phone from res_user WHERE id = " + user_id + ";";
			} else {
				sql = "SELECT id, first_name, last_name, username, password, email, phone from res_user;";
			}

			try {
				response.put("result", DatabaseHelper.getInstance().read(sql));
				return response.toJSONString();
			} catch(Exception ex) {
				ex.printStackTrace();
				response.put("error", ex.toString());
				return response.toJSONString();
			}
			
		} else {
			response.put("error", "Invalid token");
			return response.toJSONString();
		}
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@Path("/create")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createUser(String user_string, @HeaderParam("auth") String token) {
		JSONObject response = new JSONObject();
		JSONObject user;
		try {
			JSONParser parser = new JSONParser();
			user = (JSONObject) parser.parse(user_string);
		} catch(Exception ex) {
			ex.printStackTrace();
			user = new JSONObject();
		}
		if(!user.containsKey("first_name") || !user.containsKey("last_name") || !user.containsKey("password") || (!user.containsKey("email") && !user.containsKey("phone")) ) {
			response.put("error", "'first_name', 'last_name', 'password', 'email' and 'phone' are required.");
			return response.toJSONString();
		}
		
		String check_sql = "SELECT * FROM res_user where username = '" +user.get("username") + "';";
		JSONArray array = new JSONArray();
		try {
			array = DatabaseHelper.getInstance().read(check_sql);
		} catch(Exception ex) {
			
		}
		if( array.size() > 0 ) {
			response.put("error", "'username' already exists");
			return response.toJSONString();
		};
		
		String sql = "INSERT INTO res_user ";
		String values = " VALUES ( ";
		String columns = " ( ";
		if(user.containsKey("first_name")) {
			columns = columns.concat("first_name,");
			values = values.concat("" + "'" + user.get("first_name") + "'");
		}
		
		if(user.containsKey("last_name")) {
			columns = columns.concat("last_name,");
			values = values.concat("," + "'" + user.get("last_name") + "'");
		}
		
		if(user.containsKey("username")) {
			columns = columns.concat("username,");
			values = values.concat("," + "'" + user.get("username") + "'");
		}
		
		if(user.containsKey("email")) {
			columns = columns.concat("email,");
			values = values.concat("," + "'" + user.get("email") + "'");
		}
		
		if(user.containsKey("phone")) {
			columns = columns.concat("phone,");
			values = values.concat("," + "'" + user.get("phone") + "'");
		}
		
		if(user.containsKey("password")) {
			columns = columns.concat("password");
			String password_hash = "";
			try {
				password_hash = AuthenticationHelper.get_SHA_1_Secure((String)user.get("password"));
			} catch(Exception ex) {
				ex.printStackTrace();
				response.put("error", ex.toString());
				return response.toJSONString();
			}
			values = values.concat("," + "'" + password_hash + "'");
		}
		
		if(!values.equalsIgnoreCase("VALUES ( ") && !columns.equalsIgnoreCase(" ( ")) {
			columns = columns.concat(") ");
			values = values.concat(")");
			sql = sql.concat(columns);
			sql = sql.concat(values + ";");
		}
		
		try {
			response.put("result", DatabaseHelper.getInstance().executeRawUpdate(sql));
			return response.toJSONString();
		} catch(Exception ex) {
			ex.printStackTrace();
			response.put("error", ex.toString());
			return response.toJSONString();
		}
	}
	
	@SuppressWarnings("unchecked")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteUser(String user_str, @HeaderParam("auth") String token) {
		boolean authenticated;
		JSONObject response = new JSONObject();
		JSONObject user = new JSONObject();
		try {
			authenticated = new AuthenticationHelper().authenticate(token);
		
			if(authenticated) {
				JSONParser parser = new JSONParser();
				user = (JSONObject) parser.parse(user_str);
				if(user.containsKey("user_id")) {
					String sql = "DELETE FROM res_user WHERE id = " + user.get("user_id").toString();
					try {
						response.put("result", DatabaseHelper.getInstance().executeRawUpdate(sql));
					} catch(Exception ex) {
						ex.printStackTrace();
						response.put("error", ex.toString());
					}
					return response.toJSONString(); 					
				} else {
					response.put("error", "'user_id' required.");
					return response.toJSONString();
				}
			} else {
				response.put("error", "Invalid token");
				return response.toJSONString();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			response.put("error", ex.toString());
			return response.toJSONString();
		}
	}
}
