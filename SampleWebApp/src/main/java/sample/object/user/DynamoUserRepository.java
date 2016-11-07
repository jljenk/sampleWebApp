package sample.object.user;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;





/**
 * Implementation of the ICardRepository using DynamoDB as the persistence
 * layer.
 *
 */
public class DynamoUserRepository implements IUserRepository {

	public static final int EXPIRATION_IN_MINUTES = 120;
	public static final int LOCK_LIMIT = 4;
    private DynamoDBMapper mapper;
   

    public DynamoUserRepository(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    @Override
	public LoginResponse authenticate(User user, String remoteHost) {
    	String token = null;
    	Boolean success = null;
    	String error = null;

        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withHashKeyValues(user);

        List<User> results = mapper.query(User.class, queryExpression);

        if (!results.isEmpty()) {
        	User returnedUser = results.get(0);
        	
        	if(returnedUser.getLockCount()<LOCK_LIMIT){
        	
        	Long time = new Date().getTime();
        	if(returnedUser.getPassHash().equals(user.getPassHash())){
        		returnedUser.setExpiration(time);
        		returnedUser.setToken(UUID.randomUUID().toString());
            	token = returnedUser.getToken();
            	success = true;
            	returnedUser.setLockCount(0);
        	}else{
        		success = false;
        		returnedUser.setLockCount(returnedUser.getLockCount()+1);
        		
        		error = "Invalid Password";
        		
        	}
        	
        	
        	returnedUser.addLog(remoteHost, time, success);
        	mapper.save(returnedUser);
        	}else{
        		success = false;
        		error = "Account Locked";
        	}
        	
        }else{
        	success = false;
    		error = "Invalid User Name";
        }
        
        
        
        return new LoginResponse(success, error, token);
        
	}
    
    @Override
	public void create(User user) {
    	mapper.save(user);
	}


	@Override
	public String valid(String token) {
		String org = null;
		 User key = new User();
		 key.setToken(token);
		 
		 Calendar calendar = Calendar.getInstance();
		 calendar.add(Calendar.MINUTE, -EXPIRATION_IN_MINUTES);
		 Date result = calendar.getTime();
		    
		 


		 DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
	                .withIndexName(User.TOKEN_INDEX)
	        		.withHashKeyValues(key)
	        		.withConsistentRead(false);

		 
		PaginatedQueryList<User> results = mapper.query(User.class, queryExpression);
	    if(!results.isEmpty() && results.get(0).getExpiration()>result.getTime()){
    		org = results.get(0).getOrg();
    		System.out.println(org);
	    }
	    	
	    
		return org;
	}


}
