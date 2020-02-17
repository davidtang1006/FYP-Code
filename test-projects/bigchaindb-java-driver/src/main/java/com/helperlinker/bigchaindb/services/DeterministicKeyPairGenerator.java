package com.helperlinker.bigchaindb.services;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Hashtable;
import java.util.Random;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSAGenParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

/**
 * This class is a modification of net.i2p.crypto.eddsa.KeyPairGenerator. The
 * original one is not capable of generating deterministic key pairs, so it is
 * cloned and modified to achieve this.
 */
public final class DeterministicKeyPairGenerator extends KeyPairGeneratorSpi {
	static final int DEFAULT_KEYSIZE = 256; // Originally, this is private
	private EdDSAParameterSpec edParams;
	private Random random1; // Originally, this is SecureRandom
	private Random random2;
	private boolean initialized;

	private static final Hashtable<Integer, AlgorithmParameterSpec> edParameters;

	static {
		edParameters = new Hashtable<Integer, AlgorithmParameterSpec>();
		edParameters.put(Integer.valueOf(256), new EdDSAGenParameterSpec("Ed25519"));
	}

	// This is a copy of initialize(int keysize, SecureRandom random) except Random
	// is used instead of SecureRandom.
	public void initialize(int keysize, Random random1, Random random2) {
		AlgorithmParameterSpec edParams = edParameters.get(Integer.valueOf(keysize));
		if (edParams == null)
			throw new InvalidParameterException("unknown key type.");
		try {
			initialize(edParams, random1, random2);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidParameterException("key type not configurable.");
		}
	}

	// This implements the inherited abstract method
	public void initialize(int keysize, SecureRandom random) {
		AlgorithmParameterSpec edParams = edParameters.get(Integer.valueOf(keysize));
		if (edParams == null)
			throw new InvalidParameterException("unknown key type.");
		try {
			initialize(edParams, random);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidParameterException("key type not configurable.");
		}
	}

	// This is a copy of initialize(AlgorithmParameterSpec params, SecureRandom
	// random) except Random is used instead of SecureRandom.
	public void initialize(AlgorithmParameterSpec params, Random random1, Random random2)
			throws InvalidAlgorithmParameterException {
		if (params instanceof EdDSAParameterSpec) {
			edParams = (EdDSAParameterSpec) params;
		} else if (params instanceof EdDSAGenParameterSpec) {
			edParams = createNamedCurveSpec(((EdDSAGenParameterSpec) params).getName());
		} else
			throw new InvalidAlgorithmParameterException("parameter object not a EdDSAParameterSpec");

		this.random1 = random1;
		this.random2 = random2;
		initialized = true;
	}

	@Override
	public void initialize(AlgorithmParameterSpec params, SecureRandom random)
			throws InvalidAlgorithmParameterException {
		if (params instanceof EdDSAParameterSpec) {
			edParams = (EdDSAParameterSpec) params;
		} else if (params instanceof EdDSAGenParameterSpec) {
			edParams = createNamedCurveSpec(((EdDSAGenParameterSpec) params).getName());
		} else
			throw new InvalidAlgorithmParameterException("parameter object not a EdDSAParameterSpec");

		this.random1 = random;
		initialized = true;
	}

	// Random is used instead of SecureRandom
	public KeyPair generateKeyPair() {
		if (!initialized)
			initialize(DEFAULT_KEYSIZE, new Random(), new Random());

		int seedSize = edParams.getCurve().getField().getb() / 8;
		System.out.println(seedSize);
		byte[] seed1 = new byte[seedSize / 2];
		random1.nextBytes(seed1);
		byte[] seed2 = new byte[seedSize - seedSize / 2];
		random2.nextBytes(seed2);
		byte[] seed = new byte[seedSize];
		System.arraycopy(seed1, 0, seed, 0, seedSize / 2);
		System.arraycopy(seed2, 0, seed, seedSize / 2, seedSize - seedSize / 2);

		EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(seed, edParams);
		EdDSAPublicKeySpec pubKey = new EdDSAPublicKeySpec(privKey.getA(), edParams);

		return new KeyPair(new EdDSAPublicKey(pubKey), new EdDSAPrivateKey(privKey));
	}

	/**
	 * Create an EdDSANamedCurveSpec from the provided curve name. The current
	 * implementation fetches the pre-created curve spec from a table.
	 * 
	 * @param curveName the EdDSA named curve.
	 * @return the specification for the named curve.
	 * @throws InvalidAlgorithmParameterException if the named curve is unknown.
	 */
	protected EdDSANamedCurveSpec createNamedCurveSpec(String curveName) throws InvalidAlgorithmParameterException {
		EdDSANamedCurveSpec spec = EdDSANamedCurveTable.getByName(curveName);
		if (spec == null) {
			throw new InvalidAlgorithmParameterException("unknown curve name: " + curveName);
		}
		return spec;
	}
}
