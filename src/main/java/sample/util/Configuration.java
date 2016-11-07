package sample.util;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.SymmetricStaticProvider;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNSClient;

import sample.object.card.DynamoCardRepository;
import sample.object.card.ICardRepository;
import sample.object.user.DynamoUserRepository;
import sample.object.user.IUserRepository;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

public class Configuration extends CompositeConfiguration {
	

	
	private static final Logger logger = LogManager.getLogger(Configuration.class);

	public static final String CEK_FILE= "cek_file";
	public static final String MACKEY_FILE ="mackey_file";
	public static final String CEK_FILE_ALG= "cek_file_alg";
	public static final String MACKEY_FILE_ALG ="mackey_file_alg";
    public static final String PROFILE = "profile";
    public static final String ENVIRONMENT = "environment"; // Test or Prod
    public static final String AWS_REGION = "region";
    public static final String DYNAMO_ENDPOINT = "dynamodbEndpoint";


    public static final String DEFAULT_CONFIG = "Properties";
    public static final String DEFAULT_PROFILE_NAME = "default";
    public static final String DEFAULT_AWS_REGION = "us-east-1";

    private SecretKey cek; // Content encrypting key;	
	private SecretKey macKey;    // Signing key
    private Region dynamoDBRegion;
    private AmazonS3 s3Client;
    private DynamoDB dynamoDB;
    private DynamoDBMapper dynamoDBMapper;
    private AmazonDynamoDBClient dynamoDBClient;
    private AmazonSNSClient snsClient;
    
    private ICardRepository iCardRepository;
    private IUserRepository iUserRepository;

    public Configuration() {
        addConfiguration(new SystemConfiguration());

        // Attempt to load the default configuration .properties file, if no
        // configuration is found, we won't use it
        try {
            URL file = getClass().getClassLoader().getResource(DEFAULT_CONFIG);
            addConfiguration(new PropertiesConfiguration(file));
        } catch (ConfigurationException e) {
        	logger.error("No default Track.properties files found.");
        }
    }

    public Configuration(String fileName) throws ConfigurationException {
        addConfiguration(new PropertiesConfiguration(fileName));
    }

    public Region getRegion() {
        if (dynamoDBRegion == null) {
            dynamoDBRegion = RegionUtils.getRegion(getString(AWS_REGION, DEFAULT_AWS_REGION));
        }
        return dynamoDBRegion;
    }

    public AmazonS3 getS3Client() {
        if (s3Client == null) {
            s3Client = new AmazonS3Client(getCredentialsProvider());
        }
        return s3Client;
    }

    public DynamoDBMapper getDynamoDBMapper() throws IOException, NoSuchAlgorithmException {
        if (dynamoDBMapper == null) {
        	//EncryptionMaterialsProvider provider = new SymmetricStaticProvider(getCek(), getMacKey());
            //dynamoDBMapper = new DynamoDBMapper(getDynamoDBClient(), getCoreDynamoDBMapperConfig(),new AttributeEncryptor(provider));
        	dynamoDBMapper = new DynamoDBMapper(getDynamoDBClient(), getCoreDynamoDBMapperConfig());
        }
        return dynamoDBMapper;
    }


    public AmazonSNSClient getSnsClient() {
        if (snsClient == null) {
            snsClient = new AmazonSNSClient(getCredentialsProvider())
                    .withRegion(getRegion());
        }
        return snsClient;
    }
    
    private SecretKey getCek() throws IOException, NoSuchAlgorithmException {
        if (cek == null) {
        	String key = getString(CEK_FILE);
        	String alg = getString(CEK_FILE_ALG);
        	URL url = getClass().getClassLoader().getResource(key);
        	File file = new File(url.getPath());
            cek = Keys.loadKey(file, alg);
        }
        return cek;
    }
    
    private SecretKey getMacKey() throws IOException, NoSuchAlgorithmException {
        if (macKey == null) {
        	String key = getString(MACKEY_FILE);
        	String alg = getString(MACKEY_FILE_ALG);
        	URL url = getClass().getClassLoader().getResource(key);
        	File file = new File(url.getPath());
        	macKey = Keys.loadKey(file, alg);
        }
        return macKey;
    }

    private AmazonDynamoDBClient getDynamoDBClient() {
        if (dynamoDBClient == null) {
            dynamoDBClient = new AmazonDynamoDBClient(getCredentialsProvider())
                    .withRegion(getRegion());

            // If a DynamoDB endpoint is provided, override the existing one
            String dynamoDBEndpoint = getDynamoDBEndpoint();
            if (dynamoDBEndpoint != null) {
                dynamoDBClient.setEndpoint(dynamoDBEndpoint);
            }
        }
        return dynamoDBClient;
    }

    /**
     * Returns the DynamoDB mapper object with a canonical table name pre-fix.
     *
     * @return DynamoDB mapper object
     */
    public DynamoDBMapperConfig getCoreDynamoDBMapperConfig() {
        TableNameOverride nameOverride = TableNameOverride.withTableNamePrefix(getDynamoTableNamePrefix());
        return new DynamoDBMapperConfig(nameOverride);
    }

    public AWSCredentialsProviderChain getCredentialsProvider() {
        return new AWSCredentialsProviderChain(new EnvironmentVariableCredentialsProvider(),
                new SystemPropertiesCredentialsProvider(), new InstanceProfileCredentialsProvider(),
                new ProfileCredentialsProvider(getProfile()));
    }

    public String getEnvironment() {
        return getString(ENVIRONMENT);
    }

    public String getProfile() {
        return getString(PROFILE, DEFAULT_PROFILE_NAME);
    }
    
    
    public DynamoDB getDynamoDB() {
        if (dynamoDB == null) {
            dynamoDB = new DynamoDB(getDynamoDBClient());
        }
        return dynamoDB;
    }
    
    

    /**
     * Build a canonical table name pre-fix based on the environment and
     * client ID.
     */
    public String getDynamoTableNamePrefix() {
        return getEnvironment().toLowerCase() + ".";
    }

    public String getDynamoDBEndpoint() {
        return getString(DYNAMO_ENDPOINT);
    }
    
    
    
    
    /*
     * Repository configurations
     */

    public ICardRepository getICardRepository() throws IOException, NoSuchAlgorithmException {
        if (iCardRepository == null) {
        	iCardRepository = new DynamoCardRepository(getDynamoDBMapper());
        }
        return iCardRepository;
    }
    
    public IUserRepository getIUserRepository() throws IOException, NoSuchAlgorithmException {
        if (iUserRepository == null) {
        	iUserRepository = new DynamoUserRepository(getDynamoDBMapper());
        }
        return iUserRepository;
    }






}
