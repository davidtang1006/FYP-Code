package com.helperlinker.bigchaindb.services;

import com.bigchaindb.util.Base58;
import com.helperlinker.bigchaindb.services.DeterministicKeyPairGenerator;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class SecurityServices {
	public static KeyPair generateKeyPairFromPwdAndSalt(String pwd, String salt) {
		// Prepare a seed. Extract a string with length of 16.
		String substring = calculateHash(pwd, salt, "SHA-512").substring(0, 16);
		long seed = Long.parseUnsignedLong(substring, 16); // parseLong does not parse strings as negative values

		Random random = new Random(seed);

		// Prepare the keys
		DeterministicKeyPairGenerator edDsaKpg = new DeterministicKeyPairGenerator();
		edDsaKpg.initialize(DeterministicKeyPairGenerator.DEFAULT_KEYSIZE, random);
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
