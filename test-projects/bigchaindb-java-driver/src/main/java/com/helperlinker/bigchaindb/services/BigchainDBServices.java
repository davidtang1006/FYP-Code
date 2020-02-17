package com.helperlinker.bigchaindb.services;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;
import java.util.TreeMap;

import com.bigchaindb.builders.BigchainDbConfigBuilder;
import com.bigchaindb.builders.BigchainDbTransactionBuilder;
import com.bigchaindb.constants.Operations;
import com.bigchaindb.model.FulFill;
import com.bigchaindb.model.GenericCallback;
import com.bigchaindb.model.MetaData;
import com.bigchaindb.model.Transaction;
import com.helperlinker.bigchaindb.users.Helper;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import okhttp3.Response;

/**
 * Provide services to record transactions to BigchainDB. Source: <a href=
 * "https://github.com/bigchaindb/java-bigchaindb-driver#usage">https://github.com/bigchaindb/java-bigchaindb-driver#usage</a>.
 */
public class BigchainDBServices {
	/**
	 * Configure connection URL and credentials
	 */
	public static void setConfig() {
		BigchainDbConfigBuilder.baseUrl("http://localhost:9984/") // Or, use http://testnet.bigchaindb.com
				.addToken("app_id", "").addToken("app_key", "").setup();
		// The token is for authorization, but it is likely that this is not used. It
		// can be accessed by BigChainDBGlobals.getAuthorizationTokens(), but the
		// function is not called.
	}

	/**
	 * Create a record for a new helper in BigchainDB
	 * 
	 * @return Transaction ID from BigchainDB
	 */
	public static String createHelperAccount(Helper helper) {
		// Create new asset. TreeMap stores a red–black tree.
		@SuppressWarnings("serial")
		Map<String, String> assetData = new TreeMap<String, String>() {
			{
				put("userType", "HELPER");
				put("idCardNum", helper.credentials.getIdCardNum());
				put("hashedPwd", helper.credentials.getHashedPwd());
				put("salt", helper.credentials.getSalt());
			}
		};
		System.out.println("(*) Assets Prepared");

		// Create metadata (which cannot be empty)
		MetaData metaData = new MetaData();
		metaData.setMetaData("selfIntro", "");
		System.out.println("(*) Metadata Prepared");

		// Execute CREATE transaction
		try {
			String txId = doCreate(assetData, metaData, helper.credentials.getKeyPair());

			// Let the transaction commit in block
			Thread.sleep(1000);

			return txId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Update helper information in BigchainDB
	 * 
	 * @return Transaction ID from BigchainDB
	 */
	public static String updateHelperInfo(Helper helper, String selfIntro) {
		// Create transfer metadata
		MetaData transferMetadata = new MetaData();
		transferMetadata.setMetaData("selfIntro", selfIntro);
		System.out.println("(*) Transfer Metadata Prepared");

		// Execute TRANSFER transaction on the CREATED asset
		String firstTxId = helper.info.getFirstTxId();
		String latestTxId = helper.info.getLatestTxId();
		KeyPair keyPair = helper.credentials.getKeyPair();
		try {
			latestTxId = doTransfer(firstTxId, latestTxId, transferMetadata, keyPair);

			// Let the transaction commit in block
			Thread.sleep(1000);

			return latestTxId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Perform a CREATE transaction on BigchainDB network
	 * 
	 * @param assetData Data to store as asset
	 * @param metaData  Data to store as metadata
	 * @param keys      Keys to sign and verify transaction
	 * @return ID of CREATED asset
	 */
	private static String doCreate(Map<String, String> assetData, MetaData metaData, KeyPair keys) throws Exception {
		try {
			// Build and send CREATE transaction
			Transaction transaction = null;

			transaction = BigchainDbTransactionBuilder.init().addAssets(assetData, TreeMap.class).addMetaData(metaData)
					.operation(Operations.CREATE)
					.buildAndSign((EdDSAPublicKey) keys.getPublic(), (EdDSAPrivateKey) keys.getPrivate())
					.sendTransaction(handleServerResponse());

			System.out.println("(*) CREATE Transaction sent - " + transaction.getId());
			return transaction.getId();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Perform TRANSFER operations on CREATED assets
	 * 
	 * @param firstTxId  ID of the first transaction
	 * @param latestTxId ID of the latest transaction
	 * @param metaData   Data to append for this transaction
	 * @param keys       Keys to sign and verify transactions
	 * @return Transaction ID from BigchainDB
	 */
	private static String doTransfer(String firstTxId, String latestTxId, MetaData metaData, KeyPair keys)
			throws Exception {
		try {
			// Specify the previous output
			FulFill fulfill = new FulFill();
			fulfill.setOutputIndex(0);
			fulfill.setTransactionId(latestTxId);

			// Build and send TRANSFER transaction. Note that the transaction ID of the
			// CREATE transaction is passed to addAssets.
			Transaction transaction = BigchainDbTransactionBuilder.init()
					.addInput(null, fulfill, (EdDSAPublicKey) keys.getPublic())
					.addOutput("1", (EdDSAPublicKey) keys.getPublic()).addAssets(firstTxId, String.class)
					.addMetaData(metaData).operation(Operations.TRANSFER)
					.buildAndSign((EdDSAPublicKey) keys.getPublic(), (EdDSAPrivateKey) keys.getPrivate())
					.sendTransaction(handleServerResponse());

			System.out.println("(*) TRANSFER Transaction sent - " + transaction.getId());
			return transaction.getId();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static GenericCallback handleServerResponse() {
		// Define callback methods to verify response from BigchainDBServer
		GenericCallback callback = new GenericCallback() {
			@Override
			public void transactionMalformed(Response response) {
				System.out.println("malformed " + response.message());
				onFailure();
			}

			@Override
			public void pushedSuccessfully(Response response) {
				System.out.println("pushedSuccessfully");
				onSuccess(response);
			}

			@Override
			public void otherError(Response response) {
				System.out.println("otherError " + response.message());
				onFailure();
			}
		};
		return callback;
	}

	private static void onSuccess(Response response) {
		// TODO: Add your logic here with response from server
		System.out.println("Transaction posted successfully");
	}

	private static void onFailure() {
		// TODO: Add your logic here
		System.out.println("Transaction failed");
	}
}
