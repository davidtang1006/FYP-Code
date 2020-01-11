package com.helperlinker.bigchaindb.users;

import java.security.KeyPair;

class UserCredentials {
	String hashedPwd, salt;
	KeyPair keyPair;

	UserCredentials(String hashedPwd, String salt, KeyPair keyPair) {
		this.hashedPwd = hashedPwd;
		this.salt = salt;
		this.keyPair = keyPair;
	}
}
