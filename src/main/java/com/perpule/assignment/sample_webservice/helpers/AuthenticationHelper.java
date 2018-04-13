package com.perpule.assignment.sample_webservice.helpers;

import javax.ws.rs.Consumes;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AuthenticationHelper {
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String login(String user) {
		JSONObject response = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject userObj = (JSONObject) parser.parse(user);
			
			if(userObj.containsKey("username") && userObj.containsKey("password")) {
				String sql = "SELECT password FROM customer WHERE username = ".concat("'" + userObj.get("username") + "'");
				JSONObject read = DatabaseHelper.getInstance().read(sql);
				if(read != null && read.containsKey("password")) {
					if(((String)read.get("password")).equalsIgnoreCase((String)userObj.get("password"))) {
						
					}
				} else {
					response.put("error", "'password' not found.");
					return response.toJSONString();	
				}
			} else {
				response.put("error", "'username' required.");
				return response.toJSONString();
			}
            
            
        } catch(Exception e) {
            e.printStackTrace();
        }
		return new JSONObject().toJSONString();
	}
	
	public String encrypt(String object) throws Exception {
		String text = "Hello World";
        String key = "Bar12345Bar12345"; // 128 bit key
        // Create key and cipher
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        // encrypt the text
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(text.getBytes());
		return "";
	}
	
	public String encrypt(JSONObject object)  throws Exception {
		
		String text = "Hello World";
        String key = "Bar12345Bar12345"; // 128 bit key
        // Create key and cipher
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        // encrypt the text
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(text.getBytes());
		return "";
	}
	
	public String decrypt(String object)  throws Exception {

        byte[] encrypted = object.getBytes();
		String text = "Hello World";
        String key = "Bar12345Bar12345"; // 128 bit key
        // Create key and cipher
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        System.err.println(new String(encrypted));
        // decrypt the text
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        String decrypted = new String(cipher.doFinal(encrypted));
        System.err.println(decrypted);
		return "";
	}
}
