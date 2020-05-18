# Explanation of DeterministicKeyPairGenerator.java (com.helperlinker.bigchaindb.services)

This class is a modification of net.i2p.crypto.eddsa.KeyPairGenerator. The original one is not capable of generating deterministic key pairs, so it is cloned and modified to achieve this.

Concerning the modification, the original class's random source is from `SecureRandom` class. From the documentation (<https://docs.oracle.com/javase/8/docs/api/java/security/SecureRandom.html>), the class provides method `setSeed`, but it does not make the output deterministic. The description of `setSeed` reads
> The given seed supplements, rather than replaces, the existing seed. Thus, repeated calls are guaranteed never to reduce randomness.

Therefore, in `DeterministicKeyPairGenerator`, `SecureRandom` classes are replaced by `Random` classes.

(Actually, `SecureRandom` can generate deterministic results.)
