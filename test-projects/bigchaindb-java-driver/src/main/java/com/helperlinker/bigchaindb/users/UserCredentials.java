package com.helperlinker.bigchaindb.users;

import java.security.KeyPair;

class UserCredentials {
	String idCardNum, hashedPwd, salt;
	KeyPair keyPair;

	UserCredentials(String idCardNum, String hashedPwd, String salt, KeyPair keyPair) {
		this.idCardNum = idCardNum;
		this.hashedPwd = hashedPwd;
		this.salt = salt;
		this.keyPair = keyPair;
	}
}
