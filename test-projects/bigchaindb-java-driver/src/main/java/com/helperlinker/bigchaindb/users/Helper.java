package com.helperlinker.bigchaindb.users;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import com.helperlinker.bigchaindb.services.BigchainDBServices;
import com.helperlinker.bigchaindb.services.SecurityServices;

/**
 * A model for helpers
 */
public class Helper {
	/**
	 * idCardNum, Credentials, Info
	 */
	private static Map<String, Object[]> map = new HashMap<String, Object[]>();

	public class Credentials extends UserCredentials {
		Credentials(String hashedPwd, String salt, KeyPair keyPair) {
			super(hashedPwd, salt, keyPair);
		}
	}

	public class Info {
		public String firstTxId = "";
		public String latestTxId = "";
		public String selfIntro = "";

//		TODO
//		String fullName
//		String nickName
//		gender
//		dateOfBirth
//		nationality
//		languages
//		portraitPhoto
//		phoneNum
//		email
//		skillSet
//		availability
//		selfIntro
//		certificate
	}

	public Helper(String idCardNum, String hashedPwd, String salt) {
		// Generate a key pair from password and salt
		KeyPair keyPair = SecurityServices.generateKeyPairFromPwdAndSalt("aaa", salt);

		Credentials credentials = new Credentials(hashedPwd, salt, keyPair);
		Info helperInfo = new Info();
		Object[] list = new Object[] { credentials, helperInfo };
		map.put(idCardNum, list);

		// Execute BigchainDB CREATE transaction
		String txId = BigchainDBServices.createHelperAccount(idCardNum);
		if (txId != null) {
			helperInfo.firstTxId = txId;
			helperInfo.latestTxId = txId;
		}
	}

	/**
	 * Execute BigchainDB TRANSFER transaction
	 */
	public static void updateInfo(String idCardNum, String selfIntro) {
		Info helperInfo = getInfo(idCardNum);
		String txId = BigchainDBServices.updateHelperInfo(idCardNum, selfIntro);
		if (txId != null) {
			helperInfo.latestTxId = txId;
		}
	}

	/*
	 * Getters
	 */

	public static String[] getHashedPwdAndSalt(String idCardNum) {
		String[] list = { getHashedPwd(idCardNum), getSalt(idCardNum) };
		return list;
	}

	public static String getHashedPwd(String idCardNum) {
		Object[] list = map.get(idCardNum);
		if (list != null) {
			return ((Credentials) list[0]).hashedPwd;
		}
		return null;
	}

	public static String getSalt(String idCardNum) {
		Object[] list = map.get(idCardNum);
		if (list != null) {
			return ((Credentials) list[0]).salt;
		}
		return null;
	}

	public static KeyPair getKeyPair(String idCardNum) {
		Object[] list = map.get(idCardNum);
		return ((Credentials) list[0]).keyPair;
	}

	public static Info getInfo(String idCardNum) {
		Object[] list = map.get(idCardNum);
		return (Info) list[1];
	}
}
