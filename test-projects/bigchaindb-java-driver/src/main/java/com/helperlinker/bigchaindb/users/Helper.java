package com.helperlinker.bigchaindb.users;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import com.helperlinker.bigchaindb.services.BigchainDBServices;
import com.helperlinker.bigchaindb.services.SecurityServices;

public class Helper {
	/**
	 * ID card number, {hashed password, salt, key pair}
	 */
	private static Map<String, Object[]> map = new HashMap<String, Object[]>();

	private String selfIntro; // TODO: selfIntro is not used

//	TODO
//	String fullName
//	String nickName
//	gender
//	dateOfBirth
//	nationality
//	languages
//	portraitPhoto
//	phoneNum
//	email
//	skillSet
//	availability
//	selfIntro
//	certificate

	public Helper(String idCardNum, String hashedPwd, String salt) {
		// Generate a key pair from password and salt
		KeyPair keyPair = SecurityServices.generateKeyPairFromPwdAndSalt("aaa", salt);

		Object[] list = new Object[] { hashedPwd, salt, keyPair };
		map.put(idCardNum, list);

		// Execute BigchainDB CREATE transaction
		BigchainDBServices.createHelperAccount(idCardNum);
	}

	/**
	 * @return An array of hashed password and salt in order
	 */
	public static Object[] getHashedPwdAndSalt(String idCardNum) {
		Object[] list = map.get(idCardNum);
		return list;
	}

	public static String getHashedPwd(String idCardNum) {
		Object[] list = map.get(idCardNum);
		return (String) list[0];
	}

	public static String getSalt(String idCardNum) {
		Object[] list = map.get(idCardNum);
		return (String) list[1];
	}

	public static KeyPair getKeyPair(String idCardNum) {
		Object[] list = map.get(idCardNum);
		return (KeyPair) list[2];
	}
}
