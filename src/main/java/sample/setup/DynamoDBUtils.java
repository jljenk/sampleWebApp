package sample.setup;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import sample.object.card.Card;
import sample.object.user.User;
import sample.util.Configuration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Tools for handling table creation and deletion.
 *
 */
public class DynamoDBUtils {

	private static final Logger logger = LogManager.getLogger(DynamoDBUtils.class);
	
	private DynamoDBUtils() {

	}    

    public static void buildDB(Configuration config) throws InterruptedException, IOException, NoSuchAlgorithmException {
        DynamoDBMapper mapper = config.getDynamoDBMapper();
        DynamoDB dynamoDB = config.getDynamoDB();

        createTable(User.class, mapper, dynamoDB, 2L, 2L);
        createTable(Card.class, mapper, dynamoDB, 2L, 2L);

    }

    public static void destroyDB(Configuration config) throws InterruptedException, IOException, NoSuchAlgorithmException {
        // just to double check that we never delete production
        if ("prod".equals(config.getEnvironment())) {
            return;
        }

        DynamoDBMapper mapper = config.getDynamoDBMapper();
        DynamoDB dynamoDB = config.getDynamoDB();


        deleteTable(User.class, mapper, dynamoDB);
        deleteTable(Card.class, mapper, dynamoDB);

    }

    public static void createTable(Class tableClass, DynamoDBMapper mapper, DynamoDB client,
                                   long readCapacityUnits, long writeCapacityUnits) throws InterruptedException {

        Projection projection = new Projection().withProjectionType(ProjectionType.ALL);
        ProvisionedThroughput throughput = new ProvisionedThroughput()
                .withReadCapacityUnits(readCapacityUnits)
                .withWriteCapacityUnits(writeCapacityUnits);

        CreateTableRequest request = mapper.generateCreateTableRequest(tableClass)
                .withProvisionedThroughput(throughput);

        logger.info("Creating table..." + request.getTableName());

        // Local Indexes
        if (request.getLocalSecondaryIndexes() != null) {
            for (LocalSecondaryIndex index : request.getLocalSecondaryIndexes()) {
                index.setProjection(projection);
            }
        }

        // Secondary Indexes
        if (request.getGlobalSecondaryIndexes() != null) {
            for (GlobalSecondaryIndex index : request.getGlobalSecondaryIndexes()) {
                index.setProvisionedThroughput(throughput);
                index.setProjection(projection);
            }
        }

        client.createTable(request).waitForActive();
    }

    public static void deleteTable(Class tableClass, DynamoDBMapper mapper, DynamoDB client) {
        String tableName = getTableName(tableClass, mapper);
        Table table = client.getTable(tableName);

        logger.info("Deleting table..." + tableName);

        try {
            table.delete();
            table.waitForDelete();
        } catch (Exception e) {
            logger.error("DeleteTable request failed for " + tableName);
            logger.error(e.getMessage());
        }
    }

    private static String getTableName(Class tableClass, DynamoDBMapper mapper) {
        // Not sure if there is a better way of getting the table name from
        // the table class. This seems rather dumb that we have to do it this
        // way.
        CreateTableRequest request = mapper.generateCreateTableRequest(tableClass);
        return request.getTableName();
    }





}
