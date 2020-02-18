package com.helperlinker.bigchaindb.users;

import java.security.KeyPair;

import com.helperlinker.bigchaindb.services.BigchainDBServices;
import com.helperlinker.bigchaindb.services.MongoDBServices;
import com.helperlinker.bigchaindb.services.SecurityServices;

import org.bson.Document;

/**
 * A model for helpers
 */
public class Helper {
	/**
	 * idCardNum, Credentials, Info
	 */

	public Credentials credentials;
	public Info info;

	public class Credentials extends UserCredentials {
		private Credentials(String idCardNum, String hashedPwd, String salt, KeyPair keyPair) {
			super(idCardNum, hashedPwd, salt, keyPair);
		}

		/*
		 * Getters
		 */

		public String getIdCardNum() {
			return idCardNum;
		}

		public String getHashedPwd() {
			return hashedPwd;
		}

		public String getSalt() {
			return salt;
		}

		public KeyPair getKeyPair() {
			return keyPair;
		}
	}

	public class Info {
		private String firstTxId = "";
		private String latestTxId = "";
		private String selfIntro = "";

		// TODO
		// String fullName
		// String nickName
		// gender
		// dateOfBirth
		// nationality
		// languages
		// portraitPhoto
		// phoneNum
		// email
		// skillSet
		// availability
		// selfIntro
		// certificate

		/*
		 * Getters
		 */

		public String getFirstTxId() {
			return firstTxId;
		}

		public String getLatestTxId() {
			return latestTxId;
		}

		public String getSelfIntro() {
			return selfIntro;
		}
	}

	/**
	 * This constructor is used when creating a new helper at the beginning.
	 */
	public Helper(String idCardNum, String pwd, String salt) {
		String hashedPwd = SecurityServices.calculateHash(pwd, salt, "SHA-256");

		// Generate a key pair from pwd and salt
		KeyPair keyPair = SecurityServices.generateKeyPairFromPwdAndSalt(pwd, salt);

		credentials = new Credentials(idCardNum, hashedPwd, salt, keyPair);
		info = new Info();

		// Execute BigchainDB CREATE transaction
		String txId = BigchainDBServices.createHelperAccount(this);
		if (txId != null) {
			info.firstTxId = txId;
			info.latestTxId = txId;
		}
	}

	/**
	 * This constructor is used after a helper has logged in. The credentials are
	 * rediscovered. The constructor also retrieves helper's latest information from
	 * the database.
	 */
	public Helper(String idCardNum, String pwd) {
		Document assetDoc = MongoDBServices.getHelperAssetDoc(idCardNum);
		Document latestInfo = MongoDBServices.getHelperLatestInfo(idCardNum);

		String salt = ((Document) assetDoc.get("data")).getString("salt");
		String hashedPwd = SecurityServices.calculateHash(pwd, salt, "SHA-256");
		KeyPair keyPair = SecurityServices.generateKeyPairFromPwdAndSalt(pwd, salt);

		credentials = new Credentials(idCardNum, hashedPwd, salt, keyPair);

		info = new Info();
		info.firstTxId = latestInfo.getString("firstTxId");
		info.latestTxId = latestInfo.getString("latestTxId");
		info.selfIntro = latestInfo.getString("selfIntro");
	}

	/**
	 * Execute BigchainDB TRANSFER transaction
	 */
	public void updateInfo(String selfIntro) {
		String txId = BigchainDBServices.updateHelperInfo(this, selfIntro);
		if (txId != null) {
			info.latestTxId = txId;
		}
	}

	/*
	 * Getters
	 */

	public static String[] getHashedPwdAndSalt(String idCardNum) {
		Document result = MongoDBServices.getHelperAssetDoc(idCardNum);

		if (result != null) {
			String[] list = { ((Document) result.get("data")).getString("hashedPwd"),
					((Document) result.get("data")).getString("salt") };
			return list;
		}
		return null;
	}
}
