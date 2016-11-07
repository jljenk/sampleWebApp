package sample.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sample.object.card.Card;
import sample.object.card.ICardRepository;
import sample.util.Configuration;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/cards")
public class CardService {

    Logger log = LogManager.getLogger(CardService.class);

    @Inject
    Configuration config;
    

    @OPTIONS
    public javax.ws.rs.core.Response options() {
        return javax.ws.rs.core.Response.ok().build();
    }

    @Path("/all")
    @GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<Card> get() throws Exception {
    	ICardRepository r = config.getICardRepository();
    	return r.readAll();
    }
    
}