package sample.util;

import org.apache.commons.codec.digest.DigestUtils;

public class Hash {
	 private static final String SECRET = "85996c5c-2021-42f9-8bcc-de0975e0e09d";
	 
	 public static String getHash(String password){
		 return DigestUtils.sha512Hex(SECRET + password);
	 }
	 public static void main(String[] args) throws Exception {
		 System.out.println(getHash("user"));
	 }
}
