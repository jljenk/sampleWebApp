package sample.object.user;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DoNotEncrypt;
import com.google.common.collect.Lists;

@DynamoDBTable(tableName = "user")
public class User {
	
	public static final String TOKEN_INDEX = "token_index";
	public static final int LOG_SIZE = 100;
	private String username; // hash
	private String passHash;
	private String token; // token_index
	private Long expiration;
	private String org;
	private List<UserLog> log = Lists.newArrayList();
	private int lockCount = 0;
	
	@DynamoDBHashKey
	public String getUsername() {
		return username;
	}
	
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassHash() {
		return passHash;
	}
	public void setPassHash(String passHash) {
		this.passHash = passHash;
	}
	
	@DoNotEncrypt
	@DynamoDBIndexHashKey(globalSecondaryIndexName = TOKEN_INDEX)
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	@DoNotEncrypt
	public Long getExpiration() {
		return expiration;
	}
	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}
	
	
	public int getLockCount() {
		return lockCount;
	}

	public void setLockCount(int lockCount) {
		this.lockCount = lockCount;
	}

	
	@DynamoDBIgnore
	public void addLog(String remoteHost, Long time, Boolean success){
		UserLog u = new UserLog();
		u.setRemoteHost(remoteHost);;
		u.setTime(time);
		u.setSuccess(success);
		
		if(log.size()>LOG_SIZE){
			log.remove(0);
		}
		log.add(u);
	}

	
	
	
	
}
