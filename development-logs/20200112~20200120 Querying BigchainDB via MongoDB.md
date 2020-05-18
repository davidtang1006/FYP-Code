# 20200112~20200120 Querying BigchainDB via MongoDB

## Useful links

- [Querying BigchainDB](https://docs.bigchaindb.com/en/latest/query.html)
    - [Using MongoDB to Query BigchainDB Data](https://blog.bigchaindb.com/using-mongodb-to-query-bigchaindb-data-3fc651e0861b)
    - [MongoDB Java Driver](https://mongodb.github.io/mongo-java-driver/)
        - [MongoDB Java Driver 3.12 Documentation](https://mongodb.github.io/mongo-java-driver/3.12/)
        - [MongoDB Java Driver 3.12 API Documentation](https://mongodb.github.io/mongo-java-driver/3.12/javadoc/)
        - [MongoDB Java Driver Quick Tour](https://github.com/mongodb/mongo-java-driver/blob/master/driver-sync/src/examples/tour/QuickTour.java)

## Introduction

### Instructions to evaluate my code

- Start [Docker](https://docs.docker.com/compose/install/).
- Execute `make reset; make run;` using Git Bash under "bigchaindb-server" folder every time before running driver code.
- Run AppDriver.java.

### Example usage

Copy and paste the following lines into Eclipse console.
```txt
1
A1234
aaa
1
a0 a1 a2
Y
1
a3 a4 a5
Y
1
a6 a7 a8
Y
0
1
B1234
aaa
1
a7 a8 a9
Y
0

```

Try keywords "a0 a1" (log in as an employer). No results should be returned since the corresponding metadata is not the latest.

Try keywords "a6 a7" and "a8 a9". For the keywords "a6 a7", the result of "A1234" should appear before "B1234". For the keywords "a8 a9", the result of "B1234" should appear before "A1234".

## Preparation

### MongoDBQuickTour.java

Source: <https://github.com/mongodb/mongo-java-driver/blob/master/driver-sync/src/examples/tour/QuickTour.java>

Having the Docker running is a necessary condition for running MongoDBQuickTour.java.

#### 1

```java
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
```

Console output:
```txt
[main] INFO org.mongodb.driver.cluster - Cluster created with settings {hosts=[localhost:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500}
[main] INFO org.mongodb.driver.cluster - Cluster description not yet available. Waiting for 30000 ms before timing out
[cluster-ClusterId{value='5e24090d6821377572ddbfbd', description='null'}-localhost:27017] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:1, serverValue:11}] to localhost:27017
[cluster-ClusterId{value='5e24090d6821377572ddbfbd', description='null'}-localhost:27017] INFO org.mongodb.driver.cluster - Monitor thread successfully connected to server with description ServerDescription{address=localhost:27017, type=STANDALONE, state=CONNECTED, ok=true, version=ServerVersion{versionList=[3, 6, 16]}, minWireVersion=0, maxWireVersion=6, maxDocumentSize=16777216, logicalSessionTimeoutMinutes=30, roundTripTimeNanos=6876700}
[main] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:2, serverValue:12}] to localhost:27017
```

#### 2

```java
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
```

Console output:
```txt
{"_id": {"$oid": "5e242313b90c135cf616f9b2"}, "name": "MongoDB", "type": "database", "count": 1, "info": {"x": 203, "y": 102}}
Total # of documents after inserting 100 small ones (it should be 101): 101
```

#### 3

```java
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
```

Console output:
```txt
{"_id": {"$oid": "5e242313b90c135cf616f9b2"}, "name": "MongoDB", "type": "database", "count": 1, "info": {"x": 203, "y": 102}}
{"_id": {"$oid": "5e242313b90c135cf616f9b2"}, "name": "MongoDB", "type": "database", "count": 1, "info": {"x": 203, "y": 102}}
{"_id": {"$oid": "5e242313b90c135cf616f9b3"}, "i": 0}
{"_id": {"$oid": "5e242313b90c135cf616f9b4"}, "i": 1}
{"_id": {"$oid": "5e242313b90c135cf616f9b5"}, "i": 2}
```
...
```txt
{"_id": {"$oid": "5e242313b90c135cf616fa16"}, "i": 99}
{"_id": {"$oid": "5e242313b90c135cf616f9b2"}, "name": "MongoDB", "type": "database", "count": 1, "info": {"x": 203, "y": 102}}
{"_id": {"$oid": "5e242313b90c135cf616f9b3"}, "i": 0}
{"_id": {"$oid": "5e242313b90c135cf616f9b4"}, "i": 1}
{"_id": {"$oid": "5e242313b90c135cf616f9b5"}, "i": 2}
```
...
```txt
{"_id": {"$oid": "5e242313b90c135cf616fa16"}, "i": 99}
```

#### 4

```java
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
```

Console output:
```txt
{"_id": {"$oid": "5e242313b90c135cf616f9fa"}, "i": 71}
{"_id": {"$oid": "5e242313b90c135cf616f9e6"}, "i": 51}
{"_id": {"$oid": "5e242313b90c135cf616f9e7"}, "i": 52}
{"_id": {"$oid": "5e242313b90c135cf616f9e8"}, "i": 53}
```
...
```txt
{"_id": {"$oid": "5e242313b90c135cf616fa16"}, "i": 99}
{"_id": {"$oid": "5e242313b90c135cf616f9e6"}, "i": 51}
{"_id": {"$oid": "5e242313b90c135cf616f9e7"}, "i": 52}
{"_id": {"$oid": "5e242313b90c135cf616f9e8"}, "i": 53}
```
...
```txt
{"_id": {"$oid": "5e242313b90c135cf616fa16"}, "i": 99}
{"_id": {"$oid": "5e242313b90c135cf616f9e6"}, "i": 51}
{"_id": {"$oid": "5e242313b90c135cf616f9e7"}, "i": 52}
{"_id": {"$oid": "5e242313b90c135cf616f9e8"}, "i": 53}
```
...
```txt
{"_id": {"$oid": "5e242313b90c135cf616fa16"}, "i": 99}
{"_id": {"$oid": "5e242313b90c135cf616f9e6"}, "i": 51}
{"_id": {"$oid": "5e242313b90c135cf616f9e7"}, "i": 52}
{"_id": {"$oid": "5e242313b90c135cf616f9e8"}, "i": 53}
```
...
```txt
{"_id": {"$oid": "5e242313b90c135cf616fa16"}, "i": 99}
```

#### 5

```java
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
```

```txt
{"_id": {"$oid": "5e242313b90c135cf616fa16"}, "i": 99}
{"name": "MongoDB", "type": "database", "count": 1, "info": {"x": 203, "y": 102}}
{"_id": {"$oid": "5e242313b90c135cf616f9b4"}, "iTimes10": 10}
{"_id": {"$oid": "5e242313b90c135cf616f9b5"}, "iTimes10": 20}
{"_id": {"$oid": "5e242313b90c135cf616f9b6"}, "iTimes10": 30}
```
...
```txt
{"_id": {"$oid": "5e242313b90c135cf616fa16"}, "iTimes10": 990}
{"_id": null, "total": 4950}
```

#### 6

```java
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
```

Console output:
```txt
99
99
```

#### 7

```java
// Clean up
database.drop();

// Release resources
mongoClient.close();
```

Console output:
```txt
[main] INFO org.mongodb.driver.connection - Closed connection [connectionId{localValue:2, serverValue:14}] to localhost:27017 because the pool has been closed.
```

## Problems

### IndexOptionsConflict

```txt
Exception in thread "main" com.mongodb.MongoCommandException:
Command failed with error 85 (IndexOptionsConflict):
'Index:
{
    v: 2,
    key: {
        _fts: "text",
        _ftsx: 1
    },
    name: "metadata.selfIntro_text",
    ns: "bigchain.metadata",
    weights: {
        metadata.selfIntro: 1
    },
    default_language: "english",
    language_override: "language",
    textIndexVersion: 3
}
already exists with different options:
{
    v: 2,
    key: {
        _fts: "text",
        _ftsx: 1
    },
    name: "text",
    ns: "bigchain.metadata",
    weights: {
        $**: 1
    },
    default_language: "english",
    language_override: "language",
    textIndexVersion: 3
}'
on server 127.0.0.1:27017. The full response is
{
    "ok": 0.0,
    "errmsg": "Index: { v: 2, key: { _fts: \"text\", _ftsx: 1 }, name: \"metadata.selfIntro_text\", ns: \"bigchain.metadata\", weights: { metadata.selfIntro: 1 }, default_language: \"english\", language_override: \"language\", textIndexVersion: 3 } already exists with different options: { v: 2, key: { _fts: \"text\", _ftsx: 1 }, name: \"text\", ns: \"bigchain.metadata\", weights: { $**: 1 }, default_language: \"english\", language_override: \"language\", textIndexVersion: 3 }",
    "code": 85,
    "codeName": "IndexOptionsConflict"
}
```

The program dies on
```java
metadata.createIndex(Indexes.text("metadata.selfIntro"));
// Only self-introductions are searched. Other fields are not searched.
```

Resolve the problem by dropping indexes with `metadata.dropIndexes();` before creating the index.

### Must have $meta projection for all $meta sort keys

The program dies on
```java
MongoCursor<Document> cursor = metadata.find(text(keywords)).sort(orderBy(metaTextScore("metadata.selfIntro"))).iterator();
```

The solution: <https://mongodb.github.io/mongo-java-driver/3.12/builders/projections/#text-score>

The line is modified to be
```java
MongoCursor<Document> cursor = metadata.find(text(keywords)).projection(metaTextScore("score"))
        .sort(orderBy(metaTextScore("score"))).iterator();
```

## Implementation

### How to Connect to MongoDB

Before you can query a MongoDB database, you must connect to it, and to do that, you need to know its hostname and port.

If you're running a BigchainDB node on your local machine (e.g. for development and test), then the hostname should be localhost and the port should be 27017, unless you did something to change those values. If you're running a BigchainDB node on a remote machine and you can SSH to that machine, then the same is true.

If you're running a BigchainDB node on a remote machine and you configured its MongoDB to use authentication and to be publicly-accessible (to people with authorization), then you can probably figure out its hostname and port.

- Source: <https://docs.bigchaindb.com/en/latest/query.html>
