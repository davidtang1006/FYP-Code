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

	/**
	 * The main method is for debugging
	 */
	public static void main(String args[]) {
		setConfig();
		cleanUp();
	}

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

	public static ArrayList<Document> searchHelperLatestInfo(String keywords) {
		MongoCursor<Document> cursor = metadata.find(text(keywords)).projection(metaTextScore("score"))
				.sort(orderBy(metaTextScore("score"))).iterator();
		ArrayList<Document> results = new ArrayList<Document>();
		try {
			while (cursor.hasNext()) {
//				// For debugging
//				System.out.println(cursor.next().toJson());

				Document metadataDoc = cursor.next();
				if (!isTransferred(metadataDoc)) {
					// The corresponding transaction is not transferred, which implies that the
					// metadata is the latest
					Document result = new Document();
					result.append("idCardNum",
							((Document) getAssetDoc(metadataDoc).get("data")).getString("idCardNum"));
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

	private static boolean isTransferred(Document metadataDoc) {
		String id = metadataDoc.getString("id");
		Document transaction = transactions.find(eq("inputs.0.fulfills.transaction_id", id)).first();
//		// Alternative approach 1
//		Document transaction = transactions.find(new BsonDocument("inputs.0.fulfills.transaction_id", new BsonString(id))).first();
//		// Alternative approach 2
//		Document transaction = transactions.find(new Document("inputs.0.fulfills.transaction_id", id)).first();

		if (transaction != null) {
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
}
