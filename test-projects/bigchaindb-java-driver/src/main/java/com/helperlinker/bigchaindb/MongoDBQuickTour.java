package com.helperlinker.bigchaindb;

/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bson.Document;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * This class demonstrates the usage of the MongoDB Java driver. Source:
 * <a href=
 * "https://github.com/mongodb/mongo-java-driver/blob/master/driver-sync/src/examples/tour/QuickTour.java">https://github.com/mongodb/mongo-java-driver/blob/master/driver-sync/src/examples/tour/QuickTour.java</a>.
 */
public class MongoDBQuickTour {
	/**
	 * Run this main method to see the output of this quick example.
	 *
	 * @param args takes an optional single argument for the connection string
	 */
	public static void main(final String[] args) {
		MongoClient mongoClient;
		if (args.length == 0) {
			// Connect to the local database server
			mongoClient = MongoClients.create();
		} else {
			mongoClient = MongoClients.create(args[0]);
		}

		// Get handle to "myDb" database
		MongoDatabase database = mongoClient.getDatabase("myDb");

		// Get a handle to the "test" collection
		MongoCollection<Document> collection = database.getCollection("test");

		// Drop all the data in it
		collection.drop();

		// Make a document and insert it
		Document doc = new Document("name", "MongoDB").append("type", "database").append("count", 1).append("info",
				new Document("x", 203).append("y", 102));
		collection.insertOne(doc);

		// Get it (it's the only document in the collection since we dropped the rest
		// earlier on)
		Document myDoc = collection.find().first();
		System.out.println(myDoc.toJson());

		// Now, let's add lots of little documents to the collection so that we can
		// explore queries and cursors
		List<Document> documents = new ArrayList<Document>();
		for (int i = 0; i < 100; i++) {
			documents.add(new Document("i", i));
		}
		collection.insertMany(documents);
		System.out.println("Total # of documents after inserting 100 small ones (it should be 101): "
				+ collection.countDocuments());

		// Find the first document
		myDoc = collection.find().first();
		System.out.println(myDoc.toJson());

		// Let's get all the documents in the collection and print them out
		MongoCursor<Document> cursor = collection.find().iterator();
		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next().toJson());
			}
		} finally {
			cursor.close();
		}

		// Alternative approach
		for (Document d : collection.find()) {
			System.out.println(d.toJson());
		}

		// Now use a query to get one document out
		myDoc = collection.find(eq("i", 71)).first();
		System.out.println(myDoc.toJson());

		// Now use a range query to get a larger subset
		cursor = collection.find(gt("i", 50)).iterator();
		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next().toJson());
			}
		} finally {
			cursor.close();
		}

		// Range query with multiple constraints
		cursor = collection.find(and(gt("i", 50), lte("i", 100))).iterator();
		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next().toJson());
			}
		} finally {
			cursor.close();
		}

		// Alternative approach
		Consumer<Document> printBlock = new Consumer<Document>() {
			@Override
			public void accept(final Document document) {
				System.out.println(document.toJson());
			}
		};
		collection.find(gt("i", 50)).forEach(printBlock);
		collection.find(and(gt("i", 50), lte("i", 100))).forEach(printBlock);

		// Sorting
		myDoc = collection.find(exists("i")).sort(descending("i")).first();
		System.out.println(myDoc.toJson());

		// Projection
		myDoc = collection.find().projection(excludeId()).first();
		System.out.println(myDoc.toJson());

		// Aggregation
		collection.aggregate(asList(match(gt("i", 0)), project(Document.parse("{iTimes10: {$multiply: ['$i', 10]}}"))))
				.forEach(printBlock);

		myDoc = collection.aggregate(singletonList(group(null, sum("total", "$i")))).first();
		System.out.println(myDoc.toJson());

		// Update one
		collection.updateOne(eq("i", 10), set("i", 110));

		// Update many
		UpdateResult updateResult = collection.updateMany(lt("i", 100), inc("i", 100));
		System.out.println(updateResult.getModifiedCount());

		// Delete one
		collection.deleteOne(eq("i", 110));

		// Delete many
		DeleteResult deleteResult = collection.deleteMany(gte("i", 100));
		System.out.println(deleteResult.getDeletedCount());

		// Create index
		collection.createIndex(new Document("i", 1));

		// Clean up
		database.drop();

		// Release resources
		mongoClient.close();
	}
}
