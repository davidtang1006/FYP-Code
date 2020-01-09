package com.helperlinker.bigchaindb.services;

import com.bigchaindb.util.Base58;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import net.i2p.crypto.eddsa.KeyPairGenerator;

/**
 * Provide methods to calculate hashes and generate key pairs from passwords and
 * hashes, etc.
 */
public class SecurityServices {
	private static final int DEFAULT_KEYSIZE = 256;

	public static KeyPair generateKeyPairFromPwdAndSalt(String pwd, String salt) {
		// Prepare a seed. A hash function other than the one used to calculate hashed
		// password should be used. Otherwise, the key pairs generated will be
		// predictable.
		String hashString = calculateHash(pwd, salt, "SHA-512");
		byte[] seed = hexStringToByteArray(hashString);

		SecureRandom random = new SecureRandom(seed);

		// Prepare keys
		KeyPairGenerator edDsaKpg = new KeyPairGenerator();
		edDsaKpg.initialize(DEFAULT_KEYSIZE, random);
		KeyPair keyPair = edDsaKpg.generateKeyPair();

		System.out.println("(*) Keys Generated"); // TODO: Specify whose keys are generated
		System.out.println(Base58.encode(keyPair.getPublic().getEncoded()));
		System.out.println(Base58.encode(keyPair.getPrivate().getEncoded()));

		return keyPair;
	}

	/**
	 * Source: <a href=
	 * "https://javainterviewpoint.com/java-salted-password-hashing/">https://javainterviewpoint.com/java-salted-password-hashing/</a>
	 */
	public static String calculateHash(String pwd, String saltString, String algorithm) {
		MessageDigest md;
		try {
			// Select the message digest for the hash computation
			md = MessageDigest.getInstance(algorithm);

			byte[] salt = hexStringToByteArray(saltString);

			// Pass the salt to the digest for the computation
			md.update(salt);

			// Generate the salted hash
			byte[] hashedPassword = md.digest(pwd.getBytes(StandardCharsets.UTF_8));

			StringBuilder sb = new StringBuilder();
			for (byte b : hashedPassword)
				sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Source: <a href=
	 * "https://stackoverflow.com/a/140861">https://stackoverflow.com/a/140861</a>
	 * 
	 * @param s A hexadecimal string, e.g. "86eca66b50a2c1de3037bb619f2ec661"
	 *          (capital letters and small letters both work)
	 */
	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
