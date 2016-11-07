package unittests;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sample.object.card.Card;
import sample.object.card.ICardRepository;
import sample.object.user.IUserRepository;
import sample.object.user.User;
import sample.setup.DynamoDBUtils;
import sample.util.Configuration;
import sample.util.Hash;



/**
 * All functional tests are self-contained. To run a test, simple run this
 * file as a JUnit Test.
 * 
 * Create DB in Windows: java -jar C:\\dynamodb_local_2016-05-17\\DynamoDBLocal.jar -sharedDb -inMemory -port 9000
 * 
 */
public class UnitTests{

    private static final Logger LOG = LogManager.getLogger(UnitTests.class);
    final static Configuration config = new Configuration();

    @BeforeClass
    public static void setUp() throws Exception {
    	System.out.println(config.getDynamoDBEndpoint());
    	
        DynamoDBUtils.buildDB(config);
        
        // build user data
        IUserRepository userRepo = config.getIUserRepository();
        User user = new User();
        user.setUsername("user");
        user.setPassHash(Hash.getHash("user"));
        user.setOrg("testOrg");
        userRepo.create(user);
        
        // build card data
        ICardRepository cardRepo = config.getICardRepository();
        Card card2 = new Card();
        card2.setActive(true);
        card2.setDescription("Mc. D's Super Card");
        card2.setId("2");
        card2.setName("Mc. D");
        card2.setPunches(5);
        
        cardRepo.create(card2);
        
        Card card = new Card();
        card.setActive(true);
        card.setDescription("Cafe Rio Super Card");
        card.setId("1");
        card.setName("Cafe Rio");
        card.setPunches(10);
        
        cardRepo.create(card);
        
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	//DynamoDBUtils.destroyDB(config);
        System.out.println("Complete!");
    }

    @Test
    public void defaultTest() throws Exception {
    	ICardRepository cardRepo = config.getICardRepository();
    	List<Card> list = cardRepo.readAll();
    	System.out.println(list.size());
    	assert(list.size() > 0);
    }

}