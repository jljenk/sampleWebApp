package sample;

import org.glassfish.jersey.server.ResourceConfig;

import sample.providers.ConfigurationProvider;


public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
    	
        register(new ConfigurationProvider());
        
    }
}