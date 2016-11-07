package sample.object.card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;



/**
 * Implementation of the ICardRepository using DynamoDB as the persistence
 * layer.
 *
 */
public class DynamoCardRepository implements ICardRepository {

    private DynamoDBMapper mapper;

    public DynamoCardRepository(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }
    
    @Override
	public List<Card> readAll() {

    	Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
    	eav.put(":v1", new AttributeValue().withBOOL(true));
    	
    	DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        return mapper.scan(Card.class, scanExpression);
	}
    
    @Override
	public void create(Card card) {

    	mapper.save(card);
	}


}
