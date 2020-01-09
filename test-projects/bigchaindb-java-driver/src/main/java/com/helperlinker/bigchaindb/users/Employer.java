package com.helperlinker.bigchaindb.users;

import java.util.Map;
import java.util.HashMap;

/**
 * A model for employers
 */
public class Employer {
	private static Map<String, String[]> map = new HashMap<String, String[]>();

//	TODO
//	fullName
//
//	helperEmploymentPeriod
//	comments
//	ratings
//	referenceLetter

	public Employer(String idCardNum, String hashedPwd, String salt) {
		// TODO: Generate a key pair from password and salt
		String[] list = new String[] { hashedPwd, salt };
		map.put(idCardNum, list);
	}

	/**
	 * @return An array of hashed password and salt in order
	 */
	public static String[] getHashedPwdAndSalt(String idCardNum) {
		String[] list = map.get(idCardNum);
		return list;
	}

	public static String getHashedPwd(String idCardNum) {
		String[] list = map.get(idCardNum);
		return list[2];
	}

	public static String getSalt(String idCardNum) {
		String[] list = map.get(idCardNum);
		return list[1];
	}
}
