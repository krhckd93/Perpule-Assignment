package com.perpule.assignment.sample_webservice.customer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;

import com.perpule.assignment.sample_webservice.helpers.AuthenticationHelper;
import com.perpule.assignment.sample_webservice.helpers.DatabaseHelper;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@SuppressWarnings("unchecked")
@Path("/customer")
public class Customer {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomer(@DefaultValue("-1") @QueryParam("id") Long customer_id, @HeaderParam("auth") String token) {
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
			if(customer_id != -1 ) {
				sql = "SELECT id, first_name, last_name from customer WHERE id = " + customer_id + ";";
			} else {
				sql = "SELECT id, first_name, last_name from customer;";
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
	
	@POST
	@Path("/create")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createCustomer(String customer_string, @HeaderParam("auth") String token) {
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
			JSONObject customer;
			try {
				JSONParser parser = new JSONParser();
				customer = (JSONObject) parser.parse(customer_string);
			} catch(Exception ex) {
				ex.printStackTrace();
				customer = new JSONObject();
			}
			
			if(!customer.containsKey("first_name") || !customer.containsKey("last_name")) {
				response.put("error", "'first_name' and 'last_name' are required.");
				return response.toJSONString();
			}
			
			String sql = "INSERT INTO customer ";
			String values = " VALUES ( ";
			String columns = " ( ";
			if(customer.containsKey("first_name")) {
				columns = columns.concat("first_name,");
				values = values.concat("" + "'" + customer.get("first_name") + "'");
			}
			
			if(customer.containsKey("last_name")) {
				columns = columns.concat("last_name,");
				values = values.concat("," + "'" + customer.get("last_name") + "'");
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
		} else {
			response.put("error", "Invalid token");
			return response.toJSONString();
		}
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteCustomer(String cust_str, @HeaderParam("auth") String token) {
		boolean authenticated;
		JSONObject response = new JSONObject();
		JSONObject customer = new JSONObject();
		try {
			authenticated = new AuthenticationHelper().authenticate(token);
		
			if(authenticated) {
				JSONParser parser = new JSONParser();
				customer = (JSONObject) parser.parse(cust_str);
				if(customer.containsKey("customer_id")) {
					String sql = "DELETE FROM customer WHERE id = " + customer.get("customer_id").toString();
					try {
						response.put("result", DatabaseHelper.getInstance().executeRawUpdate(sql));
					} catch(Exception ex) {
						ex.printStackTrace();
						response.put("error", ex.toString());
					}
					return response.toJSONString(); 					
				} else {
					response.put("error", "'customer_id' required.");
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
