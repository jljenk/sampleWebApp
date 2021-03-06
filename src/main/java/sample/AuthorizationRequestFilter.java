package sample;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpHeaders;

import sample.object.user.IUserRepository;
import sample.util.Configuration;



@Provider
public class AuthorizationRequestFilter implements ContainerRequestFilter {

	@Inject
    Configuration config;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
			
		System.out.println("checking filter: "  + requestContext.getUriInfo().getPath());	
		if(!requestContext.getUriInfo().getPath().startsWith("log/")){
	
			System.out.println(requestContext.getRequest().getMethod());
			if (requestContext.getRequest().getMethod().equalsIgnoreCase("OPTIONS")) {
				//It is a CORS pre-flight request, there is no route for it, just return 200
		        requestContext.abortWith(Response.status(Response.Status.OK).build());
		        return;
		    }
			
			if (requestContext.getHeaders().get("authorization") == null) {
				System.out.println("no authorization header");
				requestContext.abortWith(					
					Response.status(Response.Status.FORBIDDEN)
	                .entity("Authorization header must be defined.")
	                .build()
		        );
				
			} else {
				String token = requestContext.getHeaderString("authorization");
				
				String orgId;
				try {
					orgId = chechAccessToken(token);
				} catch (NoSuchAlgorithmException e) {
					orgId = null;
					e.printStackTrace();
				}
				if(orgId==null){
					System.out.println("invalid token");
					requestContext.abortWith(Response
			            .status(Response.Status.UNAUTHORIZED)
			            .entity("Login has expired. Please login again.")
			            .build());
				}else{
					// Authorized
					System.out.println("Authorized");
					requestContext.setProperty("orgId", orgId);
				}
				
			}
	        
		}else{
			System.out.println("logging in");
		}
	}
	
	
	private String chechAccessToken(String token) throws NoSuchAlgorithmException, IOException{
		IUserRepository userRepo = config.getIUserRepository();
		
		return userRepo.valid(token);
	}
}
