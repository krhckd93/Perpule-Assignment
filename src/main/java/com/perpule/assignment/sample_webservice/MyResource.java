package com.perpule.assignment.sample_webservice;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

import javax.ws.rs.GET;


import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.perpule.assignment.sample_webservice.helpers.DatabaseHelper;


/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
    	
    	DatabaseHelper.getInstance().createTable();
    	// Returns the instance of InetAddress containing
        // local host name and address
    	InetAddress localhost;
    	try {
        localhost = InetAddress.getLocalHost();
        System.out.println("System IP Address : " + (localhost.getHostAddress()).trim());
    	} catch(Exception e) {
    		return e.toString();
    	}
        // Find public IP address
        String systemipaddress = "";
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");
 
            BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));
 
            // reads system IPAddress
            systemipaddress = sc.readLine().trim();
        }
        catch (Exception e)
        {
            systemipaddress = "Cannot Execute Properly";
        }
        System.out.println("Public IP Address: " + systemipaddress +"\n");
        return systemipaddress;
    }
}