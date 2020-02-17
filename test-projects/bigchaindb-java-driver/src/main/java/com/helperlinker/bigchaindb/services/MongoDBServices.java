package com.helperlinker.bigchaindb.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

import java.util.ArrayList;
import java.util.function.Consumer;

//import org.bson.BsonDocument;
//import org.bson.BsonString;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.*;

/**
 * This class provides some methods for querying MongoDB
 */
public class MongoDBServices {
	private static MongoClient mongoClient;
	private static MongoDatabase database;

	private static MongoCollection<Document> assets;
	private static MongoCollection<Document> metadata;
	private static MongoCollection<Document> transactions;

	public static Consumer<Document> printBlock = new Consumer<Document>() {
		@Override
		public void accept(final Document document) {
			System.out.println(document.toJson());
		}
	};

	public static void setConfig() {
		mongoClient = MongoClients.create("mongodb://127.0.0.1:27017");

		database = mongoClient.getDatabase("bigchain");

		assets = database.getCollection("assets");
		metadata = database.getCollection("metadata");
		transactions = database.getCollection("transactions");

		metadata.dropIndexes();
		metadata.createIndex(Indexes.text("metadata.selfIntro"));
		// Only self-introductions are searched. Other fields are not searched.
	}

	public static void cleanUp() {
		metadata.dropIndexes();
		mongoClient.close();
	}

	/**
	 * @return The helper's latest information, including idCardNum and data in
	 *         metadataDoc
	 */
	public static ArrayList<Document> searchHelperLatestInfo(String keywords) {
		MongoCursor<Document> cursor = metadata.find(text(keywords)).projection(metaTextScore("score"))
				.sort(orderBy(metaTextScore("score"))).iterator();
		ArrayList<Document> results = new ArrayList<Document>();
		try {
			while (cursor.hasNext()) {
				// // For debugging
				// System.out.println(cursor.next().toJson());

				Document metadataDoc = cursor.next();
				if (!isMetadataDocTxTransferred(metadataDoc)) {
					// The corresponding transaction is not transferred, which implies that the
					// metadataDoc is the latest

					Document assetDoc = getAssetDoc(metadataDoc);
					if (!((Document) assetDoc.get("data")).getString("userType").equals("HELPER")) {
						// Discard this result since it does not belong to a helper
						continue;
					}

					Document result = new Document();
					result.append("idCardNum", ((Document) assetDoc.get("data")).getString("idCardNum"));
					result.append("selfIntro", ((Document) metadataDoc.get("metadata")).getString("selfIntro"));

					results.add(result);
				} else {
					continue;
				}
			}
		} finally {
			cursor.close();
		}
		return results;
	}

	private static boolean isMetadataDocTxTransferred(Document metadataDoc) {
		String id = metadataDoc.getString("id");
		Document nextTransaction = transactions.find(eq("inputs.0.fulfills.transaction_id", id)).first();
		// // Alternative approach 1
		// Document nextTransaction = transactions
		// .find(new BsonDocument("inputs.0.fulfills.transaction_id", new
		// BsonString(id))).first();
		// // Alternative approach 2
		// Document nextTransaction = transactions.find(new
		// Document("inputs.0.fulfills.transaction_id", id)).first();

		if (nextTransaction != null) {
			return true;
		}
		return false;
	}

	private static boolean isTxTransferred(Document transaction) {
		String id = transaction.getString("id");
		Document nextTransaction = transactions.find(eq("inputs.0.fulfills.transaction_id", id)).first();

		if (nextTransaction != null) {
			return true;
		}
		return false;
	}

	private static Document getAssetDoc(Document metadataDoc) {
		String txId = metadataDoc.getString("id");
		Document transaction = transactions.find(eq("id", txId)).first();

		String assetId = ((Document) transaction.get("asset")).getString("id");
		Document assetDoc = assets.find(eq("id", assetId)).first();

		if (assetDoc != null) {
			return assetDoc;
		}
		return null;
	}

	private static Document getMetadataDoc(String txId) {
		Document metadataDoc = metadata.find(eq("id", txId)).first();

		if (metadataDoc != null) {
			return metadataDoc;
		}
		return null;
	}

	public static Document getHelperAssetDoc(String idCardNum) {
		return assets.find(and(eq("data.userType", "HELPER"), eq("data.idCardNum", idCardNum))).first();
	}

	/**
	 * @return The helper's latest information, including firstTxId, latestTxId and
	 *         data in metadataDoc
	 */
	public static Document getHelperLatestInfo(String idCardNum) {
		// 1. Find the assetDoc, then get the assetId (firstTxId).
		// 2. From transactions, get a cursor by the assetId, then get the latestTxId
		// 3. From the latestTxId, get the metadataDoc

		Document result = new Document();

		Document assetDoc = getHelperAssetDoc(idCardNum);
		if (assetDoc == null) {
			return null;
		}

		String assetId = assetDoc.getString("id");
		result.append("firstTxId", assetId);

		MongoCursor<Document> cursor = transactions.find(eq("asset.id", assetId)).iterator();
		try {
			if (!cursor.hasNext()) {
				// There is only one transaction, which is the CREATE transaction
				result.append("latestTxId", assetId);

				Document metadataDoc = getMetadataDoc(assetId);
				result.append("selfIntro", ((Document) metadataDoc.get("metadata")).getString("selfIntro"));
			}

			while (cursor.hasNext()) {
				// // For debugging
				// System.out.println(cursor.next().toJson());

				Document transaction = cursor.next();
				if (!isTxTransferred(transaction)) {
					// The transaction is not transferred, which implies that it is the latest

					String latestTxId = transaction.getString("id");
					result.append("latestTxId", latestTxId);

					Document metadataDoc = getMetadataDoc(latestTxId);
					result.append("selfIntro", ((Document) metadataDoc.get("metadata")).getString("selfIntro"));
				} else {
					continue;
				}
			}
		} finally {
			cursor.close();
		}
		return result;
	}
}
