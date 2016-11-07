package sample.providers;

import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import sample.util.Configuration;


@Provider
public class ConfigurationProvider extends AbstractBinder implements Factory<Configuration> {

    Logger log = LogManager.getLogger(Configuration.class);

    Configuration config = new Configuration();

    @Override
    protected void configure() {
        bindFactory(this).to(Configuration.class);
    }

    @Override
    public Configuration provide() {
        return config;
    }

    @Override
    public void dispose(Configuration instance) {
        // Nothing to do...
    }
}
