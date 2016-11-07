package sample.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sample.object.user.IUserRepository;
import sample.object.user.Login;
import sample.object.user.LoginResponse;
import sample.object.user.User;
import sample.util.Configuration;
import sample.util.Hash;

import java.net.InetSocketAddress;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.spi.http.HttpExchange;



@Path("/log")
public class LoginService {

    Logger log = LogManager.getLogger(LoginService.class);


    @Inject
    Configuration config;
    
    

    @OPTIONS
    public javax.ws.rs.core.Response options() {
        return javax.ws.rs.core.Response.ok().build();
    }

    @Path("/in")
    @POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public LoginResponse login(@Context HttpServletRequest requestContext, Login login) throws Exception {
    	
    	String ip = requestContext.getRemoteAddr();
    	System.out.println(ip);
    	
    	IUserRepository r = config.getIUserRepository();
    	User user = new User();
        
    	user.setUsername(login.getUsername());
    	user.setPassHash(Hash.getHash(login.getPassword()));
    	
    	return r.authenticate(user, ip);
    }
    
}