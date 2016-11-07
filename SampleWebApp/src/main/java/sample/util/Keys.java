package sample.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


import com.amazonaws.util.IOUtils;

public class Keys {

	public static void main (String[] args) throws IOException, NoSuchAlgorithmException{
		System.out.println(createAndSaveKey("MACKEY", 128, "AES"));
		System.out.println(createAndSaveKey("CEK",128, "AES"));
	}
	
	private static String createAndSaveKey (String name, int size, String algorithm) throws IOException, NoSuchAlgorithmException{
		SecretKey key = generateKey(size, algorithm);
		System.out.println(key.getEncoded().length);
		Date d = new Date();
		File f1 = new File("C:\\Users\\Jeff\\git\\school\\SampleWebApp\\SampleWebApp\\src\\main\\resources\\"+name+"_"+d.getTime()+".key");
		File f2 = new File("C:\\Users\\Jeff\\git\\school\\SampleWebApp\\SampleWebApp\\src\\test\\resources\\"+name+"_"+d.getTime()+".key");
		
		saveKey(key,f1);
		saveKey(key,f2);
		return f1.getName();
	}
	
	private static SecretKey generateKey(int size, String algorithm) throws NoSuchAlgorithmException
	{
	    KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
	    keyGenerator.init(size);
	    return keyGenerator.generateKey();
	}

	private static void saveKey(SecretKey key, File file) throws IOException
	{
	    FileOutputStream out = new FileOutputStream(file);
	    out.write(key.getEncoded());
	    out.close();
	}

	public static SecretKey loadKey(File file, String algorithm) throws IOException, NoSuchAlgorithmException
	{

		FileInputStream in = new FileInputStream(file);
		byte[] encoded = IOUtils.toByteArray(in);
		SecretKeySpec key = new SecretKeySpec(encoded, algorithm);
		System.out.println(key.getFormat());
		System.out.println(key.getAlgorithm());
		System.out.println(key.getEncoded().length);
	    return key;
	}
	
	
	
}
