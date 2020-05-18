# 20191229~20200102 Studying BigchainDB YouTube resources

## Useful links

- [BigchainDB Transactions Specifications](https://github.com/bigchaindb/BEPs/tree/master/13)
- [Trent McConaghy - BigchainDB: a Scalable Blockchain Database, in Python](https://www.youtube.com/watch?v=1NHHmHVCWy0)
- [BigchainDB Version 1.0 Released - Live Coding Demo](https://www.youtube.com/watch?v=zYa-GGNwxXo)
- [Troy McConaghy - BigchainDB - What's New in BigchainDB 2.0?](https://www.youtube.com/watch?v=0q3inRXxo8A)

## [Trent McConaghy - BigchainDB: a Scalable Blockchain Database, in Python](https://www.youtube.com/watch?v=1NHHmHVCWy0)

### Introduction

#### Modern Application Stacks

Storage breaks into two things: file system (hierarchy) and database (query-ability).

#### The Modern Cloud Application Stack

Applications: Facebook, Netflix, Google Maps\
Processing: EC2\
File System: S3, Google Drive\
Database: MySQL, MongoDB

#### Bitcoin Sparked a Revolution

"What if I could own digital art the way I own Bitcoin?"

#### To be Distributed, Big Data Databases Must Solve Consensus

Key terms: Byzantine Fault Tolerance, Paxos. Paxos is provably correct to maintain different nodes in a database. Apache Zookeeper bases on Paxos.

#### Two Ways to Scale up

Blockchain-ify: What if we start with something that naturally scales and give it characteristics that blockchain have?

#### Blockchain-ify

Decentralization: No single entity owns or controls.\
Distributed: Resources (including processing resources and storage resources) are shared among more than one machine.\
Assets: One can issue and transfer assets.\
Blockchain (noun, 1991): Hashed-together chain of blocks\
Blockchain (noun): Storage that is decentralized, immutable + assets\
Blockchain (adj): Decentralization + immutability + assets

We can have centralized distributed databases: Cassandra. We also have decentralized databases, where no entity owns or controls the databases.

The one that owns the asset owns a private key, which is like a password.

### Introducing BigchainDB

#### How to Blockchain-ify Big Data

We give blockchain attributes to an existing big data database. The key idea is to retain the performance of the big data database. Paxos derivative is involved. *Solving order on top by Paxos derivative is not needed.* Naturally build a log of all the transactions. Once one has the log, all the other information can be built on the top in a hierarchical fashion.

Add in blockchain characteristics. Every node in a blockchain system has a vote. All the nodes form a federation. If a transaction wants to get accepted, the majority of the node has to say yes. Otherwise, the transaction is not accepted. **Instead of doing one transaction at a time, we vote on a thousand at a time.** Group all these thousands of transactions into a block.

For assets, there are digital signature on them.

#### System Architecture

BigchainDB builds on top of an existing distributed database. The BigchainDB team **looked at and benchmarked quite a few**, including MongoDB, Cassandra and ElasticSearch. The existing databases are wildly better than any blockchain system available.

The team eventually looked at one characteristic, which is how good is the change feed mechanism. **_Change feed mechanism tells one what happened in real time._** RethinkDB is built for change feed mechanism. It is a JSON style document store built in C++.

```txt
RethinkDB cluster is wrapped by BigchainDB federation.
BigchaindDB nodes than talk to end users.
```

The team did not add any new communication mechanisms beyond what RethinkDB has (RethinkDB has its way of communicating). If node 1 wants to talk to node 2, then it is via storage.

#### Two Tables

- One table for incoming transactions
    - Incoming transactions are deterministically assigned to a given node.
    - Once there is enough transactions for a given block. The block is written to another table. *This is all within one RethinkDB instance*.
    - Other nodes are also trying to write to their blocks.
    - When one tries to write to another table, let the raft algorithm (the consensus algorithm of RethinkDB) to sort out the order. We do not impose any extra algorithms.
- Another table for the actual set of information in blockchain
    - The transactions are grouped together in the blockchain.
    - After the transactions are written, we vote if the transactions are good.
    - **(One common experience is that when one do things at scale, simplest algorithms emerge. That is the target to aim for.)**

#### Benchmarks

RethinkDB is benchmarked. Read the whitepaper.

#### Comparison

Features from traditional blockchains:
- Immutability
- Decentralized control
- Assets

Features from big data:
- High throughput
- Low latency
- High capacity
- Rich permissioning
- Query capabilities

BigchainDB has both the features from traditional blockchains and big data. BigchainDB allows different parties to control collectively. **_Consortium databases_** is a powerful concept.

#### Use Case 1

How does one know a diamond is not a blood diamond? How does one know it is not from child labour or stolen?

If there is a registry for that diamond that is being managed by all the major certification houses of the world, the insurance companies and the miners, etc., that will be nice.

BigchainDB team work with Everledger which stores data about diamond. Diamond industry is about 80-billion-dollar industry and it is estimated that there are 40% fraud. Also, the team works with eBay data.

#### Use Case 2

BigchainDB team also worked with RWE, the biggest energy provider in Germany. Laws in Germany are getting deregulated. The problem is, how to sell energy to power grid.

With BigchainDB, every single node can talk to every other node and there is no centralized controller. The team manage money flow in energy deregulation.

#### Use Case 3

How do doctors know medical journal providers/drug supply chain is legitimate? UK government create a law called Sunshine Act. Every time dollar flow from hospitals in the UK to any of the big pharmaceuticals company or any of the big scientific publisher, that has to be transparent. This can be achieved by BigchainDB. **Blockchain is a transparency engine.**

The team is working with Tangent 90.

#### Use Case 4

The speaker works for a company called Ascribe. If an artist created some digital art, how can the artist be proved to be the owner? Blockchain technology can help here.

### Python & Usage

We should not be trying to prematurely optimize things when we write things. Therefore, the team starts with Python. When we have bottlenecks, those are where we start to optimize.

#### Create a Digital Asset

1. Import crypto-library from BigchainDB and then generate a key pair. In real world, one will have it in something else.
2. There is a digital asset payload in JSON.
3. Sign the transaction with private key.
4. Write the transaction to BigchainDB server-side instances.

`condition` is like the output. `fulfillment` is like the input. After the digital asset is created, one can transfer it. The database node will check to see if the transaction is legitimate (e.g. is signed correctly). Also, double spending is prevented.

#### Decentralization of the Cloud

Centralized >> Partly Decentralized >> Fully Decentralized

Different models have different stacks. We can add one new database while keeping traditional databases. We are moving to a world where our application are more decentralized. Decentralized autonomous organization (DAO) groups applications together.

Regarding fully decentralization, there are three things:
- Ethereum, which can be viewed as scripts that are running that no one owns or controls. Ethereum has not had much for storage media.
- IPFS (Interplanetary File System)
- BigchainDB

We have a more decentralized world, Internet 3.0.

### Q&A

#### Validation is through some kind of voting system. Nodes are entities to vote. What stop one from spinning up a lot of nodes to get a lot of votes to abuse the system?

This is called the **_Sybil attack_**. That is a problem when one has a fully open network and you have unrestricted membership. For Bitcoin, one votes by electricity spent. The team solve the problem by a federation, which is a list of public keys. Consider the medical community. **The federation will just have a list of the 20 hospitals.** One can add nodes and remove nodes on the fly. **The existing nodes vote for the addition/removal of nodes.** This is just a more pragmatic solution. This is also what DAO is doing (the number of votes is proportional to how much money is put in).

#### (Described the inner workings of Bitcoin and asked if the description is correct.)

Ignore all the extra complexity that Bitcoin miners produce. **The validation is done by existing nodes. The reward is extrinsic.** The participants in the system have a collective interest in seeing the network runs and survives.

#### Do you have a tragedy of the commons situation? Who is going to spend more time to run more nodes?

The setup consists of 15, 25, 35 nodes but not 5000 nodes. They are the people in, e.g., the trade organization. They are incentivized to have the most recent version of the software. If the most recent version of the software is just a speed optimization, maybe they would not bother. In the world of blockchain, this is called **_soft fork versus hard fork_**.

Soft fork means the upgrade does not affect the protocol at all. Hard fork means everyone really has to upgrade. **If one node consistently does not upgrade, it can be removed from the list of nodes by votes from others.**

#### Have you thought about something else rather than majority votes?

There is a broad design space regarding the consensus protocol. In Bitcoin, there is longest chain rule. In Ethereum, there is GHOST protocol. You can have 2/3 votes or majority votes. You can have things weighted.

Majority votes tie nicely with theory from Byzantine fault tolerance. We have 2f+1, 3f+1. There are also some theoretical reasons.

## [BigchainDB Version 1.0 Released - Live Coding Demo](https://www.youtube.com/watch?v=zYa-GGNwxXo)

See <https://docs.bigchaindb.com/projects/server/en/latest/http-client-server-api.html>. This page describes how to query the database.

### Live Demo

JSFiddle is introduced. The user interface includes HTML part, JavaScript part and the CSS part.

## [Troy McConaghy - BigchainDB - What's New in BigchainDB 2.0?](https://www.youtube.com/watch?v=0q3inRXxo8A)

The biggest change in BigchainDB 2.0 is the change from communications being done by a database protocol to communications being done by Tendermint.

### Changes in BigchainDB Transactions

1. The version changes from 1.0 to 2.0.
2. Hash-then-fulfill >> Fulfill-then-hash
3. The message-to-sign differs for each input now. Previously, one would be signing the same message. This raises a security issue.

### Other Changes

There are also changes in HTTP API and command line interface.
