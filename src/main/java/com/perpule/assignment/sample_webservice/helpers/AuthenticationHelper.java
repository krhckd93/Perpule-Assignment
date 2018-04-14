package com.perpule.assignment.sample_webservice.helpers;

import javax.ws.rs.Consumes;

import java.security.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@SuppressWarnings("unchecked")
@Path("/login")
public class AuthenticationHelper {
	
	private Cipher ecipher;

	private Cipher dcipher;
	private static String secret_key = "ThisIsTheSecretKey";
	private SecretKey key;
	 
	
	public AuthenticationHelper() throws Exception {
		key = new SecretKeySpec(secret_key.getBytes(), "AES");
		System.out.println("Secret key" + key);
		ecipher = Cipher.getInstance("DES");
	    dcipher = Cipher.getInstance("DES");
	    ecipher.init(Cipher.ENCRYPT_MODE, key);
	    dcipher.init(Cipher.DECRYPT_MODE, key);
	}
	
	public static AuthenticationHelper getInstance() {
		try {
		AuthenticationHelper authHelper = new AuthenticationHelper();
		return authHelper;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String login(String user) {
		JSONObject response = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject paramObj = (JSONObject) parser.parse(user);
			
			if(paramObj.containsKey("username") && paramObj.containsKey("password")) {
				String sql = "SELECT * FROM customer WHERE username = ".concat("'" + paramObj.get("username") + "'");
				JSONArray read_list = DatabaseHelper.getInstance().read(sql);
				JSONObject read = new JSONObject();
				if(read_list.size() > 0) {
					read = (JSONObject) read_list.get(0);
				
					if(read.containsKey("password")) {
						if(((String)read.get("password")).equalsIgnoreCase(get_SHA_1_Secure((String)paramObj.get("password")))) {
							// Authenticate
							JSONObject auth = new JSONObject();
							auth.put("username", paramObj.get("username"));
							auth.put("password", paramObj.get("password"));
							response.put("token", encrypt(auth.toJSONString()));
							return response.toJSONString();
						} else {
							response.put("error", "Wrong password");
							return response.toJSONString();	
						}
					} else {
						response.put("error", "'password' not found.");
						return response.toJSONString();	
					}
				} else {
					response.put("error", "'username' required.");
					return response.toJSONString();
				}
			} else {
				response.put("error", "'username' required.");
				return response.toJSONString();
			}
        } catch(Exception e) {
            e.printStackTrace();
            response.put("error", e.toString());
			return response.toJSONString();
        }
	}
	
	public boolean authenticate(String token) {
		JSONParser parser = new JSONParser();
		try {
			String user_string = decrypt(token);
			JSONObject user = (JSONObject)parser.parse(user_string);
			if(user != null || !user_string.equalsIgnoreCase("")) {
				return true;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
		
	}
	
	public String encrypt(String str) throws Exception {
	    byte[] utf8 = str.getBytes("UTF8");

	    if(ecipher == null) {
			new AuthenticationHelper();
		}
	    // Encrypt
	    byte[] enc = ecipher.doFinal(utf8);

	    // Encode bytes to base64 to get a string
	    return new sun.misc.BASE64Encoder().encode(enc);
	}

	public String decrypt(String str)  throws Exception {

		byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

		if(dcipher == null) {
			new AuthenticationHelper();
		}
	    byte[] utf8 = dcipher.doFinal(dec);

	    // Decode using utf-8
	    return new String(utf8, "UTF8");
	    
	}
	
	public static String get_SHA_1_Secure(String passwordToHash)
    {
        String generatedPassword = null;
        try {
        	byte[] salt = secret_key.getBytes();
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for(int i=0; i < bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        System.out.println(passwordToHash);
        System.out.println(generatedPassword);
        return generatedPassword;
    }
	
	private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}
