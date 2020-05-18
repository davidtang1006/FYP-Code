# 20200207~20200212 Discussing blockchain databases in the literature survey

## Useful links

- [Permissions in BigchainDB](https://docs.bigchaindb.com/en/latest/permissions.html)
- [BigchainDB, Privacy and Private Data](https://docs.bigchaindb.com/en/latest/private-data.html)
- [Proof of Stake FAQ from Ethereum](https://github.com/ethereum/wiki/wiki/Proof-of-Stake-FAQ)
- [Tendermint Explained—Bringing BFT-based PoS to the Public Blockchain Domain](https://blog.cosmos.network/tendermint-explained-bringing-bft-based-pos-to-the-public-blockchain-domain-f22e274a0fdb)
- [The latest gossip on BFT consensus](https://arxiv.org/pdf/1807.04938.pdf)
- [The update items of BigchainDB 2.0](https://github.com/bigchaindb/BEPs/tree/master/13#changes-between-version-1-and-2-of-this-spec)

## [BigchainDB, Privacy and Private Data](https://docs.bigchaindb.com/en/latest/private-data.html)

### Basic Facts

- Every node in a BigchainDB network has a full copy of all the stored data.
- Everyone given access to a node via the BigchainDB HTTP API can find and read all the data stored by BigchainDB. The list of people with access might be quite short.
- If the connection between an external user and a BigchainDB node isn’t encrypted (using HTTPS, for example), then a wiretapper can read all HTTP requests and responses in transit.

### Storing Private Data Off-Chain

One can use BigchainDB to
- Keep track of who has read permissions (or other permissions) in a third-party system.

## Further Notes

- <https://github.com/ethereum/wiki/wiki/Proof-of-Stake-FAQ> provides explanations about proof of stake.
- It turns out the Tendermint appears to be quite complicated. <https://blog.cosmos.network/tendermint-explained-bringing-bft-based-pos-to-the-public-blockchain-domain-f22e274a0fdb> and <https://arxiv.org/pdf/1807.04938.pdf> are the materials for understanding it.
- [This workshop talk](./workshop-talk-3.pdf) discusses blockchain consensus schemes.
- [The update items](https://github.com/bigchaindb/BEPs/tree/master/13#changes-between-version-1-and-2-of-this-spec) in Blockchain 2.0 seems interesting. I may want to mention it later.
