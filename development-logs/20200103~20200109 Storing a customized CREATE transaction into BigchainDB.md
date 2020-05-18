# 20200103~20200109 Storing a customized CREATE transaction into BigchainDB

## Useful links

- [BigchainDB Documentation](https://docs.bigchaindb.com/en/latest/index.html)
    - [Querying BigchainDB](https://docs.bigchaindb.com/en/latest/query.html)

## Introduction

### Instructions to evaluate my code

- Start [Docker](https://docs.docker.com/compose/install/).
- If Docker is out of memory, one empirical solution is to close the browser (e.g. Google Chrome) and restart.
- Execute `make reset` to clean up the data stored in BigchainDB using Git Bash under "bigchaindb-server" folder.
- Execute `make run`.
- In Eclipse, go to "com.helperlinker.bigchaindb >> AppDriver.java" then got to "Run >> Run Configurations... >> Arguments". Include `-Dorg.slf4j.simpleLogger.defaultLogLevel=debug` in "VM arguments" field.
- Run AppDriver.java.

### Expected result

On execution of AppDriver.java, a dummy helper account is generated and is recorded by BigchainDB. The following is the corresponding console output.
```txt
(*) Keys Generated
GfHq2tTVk9z4eXgyMrBcAo9tyQBPQjo1eDPSA9FYtgQH6qHE7sJ3UaXmwK5M
2mWNaEKEJrtB1rEvHsFx8aM7szmAfVuR5y4VyYczswad39FiT5xkhYkno8brJ5nsaN
[main] INFO com.bigchaindb.builders.BigchainDbConfigBuilder - Connected to node - http://localhost:9984/ after 0 retry(s)...
(*) Assets Prepared
(*) Metadata Prepared
[main] DEBUG com.bigchaindb.api.TransactionsApi - sendTransaction Call :{"asset":{"data":{"hashedPwd":"b8db2e4680c00f5075682be1179797bf29f15f7a2b87a6818eec3cf9e47955ff","idCardNum":"A1234","salt":"a0eb205f1dfcc32fbb21c4febb8a4669","userType":"HELPER"}},"id":"e98f8d7d99fffef93483710ab6744cda792716df89585724568da71c6727f51a","inputs":[{"fulfillment":"pGSAIG_klkrF9fO3dX1_OXzRAhZt1K8ng2WicsEeEXaoY0c0gUCP1JSLcg3Wef0-ud8whOJXUbHxx9QpxQ5q0a5LBzzWopA7HJFgXvrj2N81yiVI-UPDaDI_9E6wupRia1mj4uUN","fulfills":null,"owners_before":["8XnRFECfALcWkFkyNEa4XDHqeABNVbEE26LxU5YRhpm1"]}],"metadata":{"selfIntro":""},"operation":"CREATE","outputs":[{"amount":"1","condition":{"details":{"public_key":"8XnRFECfALcWkFkyNEa4XDHqeABNVbEE26LxU5YRhpm1","type":"ed25519-sha-256"},"uri":"ni:///sha-256;PAu09qFSkz3lqAjedZOVtPM85skr8WJiNLWf43bvKOg?fpt=ed25519-sha-256&cost=131072"},"public_keys":["8XnRFECfALcWkFkyNEa4XDHqeABNVbEE26LxU5YRhpm1"]}],"version":"2.0"}
(*) CREATE Transaction sent - e98f8d7d99fffef93483710ab6744cda792716df89585724568da71c6727f51a
pushedSuccessfully
Transaction posted successfully
```

You are then asked to select an option (a simple login mechanism is implemented).
```txt
Please select an option.
1. I am a helper.
2. I am an employer.
```

There is an available account, which belongs to a helper.
- ID card number: A1234
- Password: aaa

There is currently nothing else you can do.

### Explanation of different components in the project

#### AppDriver.java

`AppDriver` provides an entry point to the console app. Firstly, a dummy account is created. I did not establish a routine to create an account. However, it should be pretty straight-forward to do so.

After the accounts is created. The user is able to select which actions to perform.

#### BigchainDBJavaDriverUsageExample.java

`BigchainDBJavaDriverUsageExample` is not part of the console app. It is from <https://github.com/bigchaindb/java-bigchaindb-driver#usage> and is runnable. I referenced to this to interact with BigchainDB.

#### Helper.java and Employer.java (com.helperlinker.bigchaindb.users)

`Helper` and `Employer` are models for helpers and employers respectively. `map` stores the credentials (ID card number, hashed password, salt and key pair). There are also getters for hashed password, salt and key pair.

There should be more fields, for example, full name, skill set, etc. for helpers.

#### BigchainDBServices.java (com.helperlinker.bigchaindb.services)

`BigchainDBServices` provides services to record transactions to BigchainDB. Source: <https://github.com/bigchaindb/java-bigchaindb-driver#usage>.

#### SecurityServices.java (com.helperlinker.bigchaindb.services)

`SecurityServices` provides methods to calculate hashes and generate key pairs from passwords and hashes, etc.

## Problems

### Docker not being started

```txt
# although bigchaindb has tendermint and mongodb in depends_on,
# launch them first otherwise tendermint will get stuck upon sending yet another log
# due to some docker-compose issue; does not happen when containers are run as daemons
Couldn't connect to Docker daemon. You might need to start Docker for Windows.
make: *** [run] Error 1
```

### The serializable class does not declare a static final `serialVersionUID` field of type long (`TreeMap`)

From the [Javadoc](http://java.sun.com/javase/6/docs/api/java/io/Serializable.html):
> The serialization runtime associates with each serializable class a version number, called a `serialVersionUID`, which is used during deserialization to verify that the *sender and receiver* of a serialized object have loaded classes for that object that are compatible with respect to serialization. If the receiver has loaded a class for the object that has a different `serialVersionUID` than that of the corresponding sender's class, then deserialization will result in an `InvalidClassException`. A serializable class can declare its own `serialVersionUID` explicitly by declaring a field named `"serialVersionUID"` that must be static, final, and of type long:
> ```txt
> ANY-ACCESS-MODIFIER static final long serialVersionUID = 42L;
> ```
>
> If a serializable class does not explicitly declare a `serialVersionUID`, then the serialization runtime will calculate a default `serialVersionUID` value for that class based on various aspects of the class, as described in the Java(TM) Object Serialization Specification. However, it is strongly recommended that all serializable classes explicitly declare `serialVersionUID` values, since the default `serialVersionUID` computation is highly sensitive to class details that may vary depending on compiler implementations, and **can thus result in unexpected `InvalidClassExceptions` during deserialization**. Therefore, to guarantee a consistent `serialVersionUID` value across different java compiler implementations, a serializable class must declare an explicit `serialVersionUID` value. It is also strongly advised that explicit `serialVersionUID` declarations use the private modifier where possible, since such declarations apply only to the immediately declaring class `serialVersionUID` fields are not useful as inherited members.

One can configure the IDE to ignore this, instead of giving a warning, or autogenerate an id.

- Source: <https://stackoverflow.com/a/2288946>
- Search term used: "The serializable class does not declare a static final serialVersionUID field of type long"

`@SuppressWarnings("serial")` is added to suppress the warning. As long as `InvalidClassExceptions` do not show up, I do not have to deal with `serialVersionUID`.

## Implementation

### Password hash calculation

```java
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashExample {
    public static void main(String[] args) {
        String password = "secret1234";

        MessageDigest md;
        try {
            // Select the message digest for the hash computation -> SHA-256
            md = MessageDigest.getInstance("SHA-256");

            // Generate the random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Pass the salt to the digest for the computation
            md.update(salt);

            // Generate the salted hash
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword)
                sb.append(String.format("%02x", b));

            System.out.println(sb);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
```

- Source: <https://javainterviewpoint.com/java-salted-password-hashing/>
- Search term used: "java salted password"

### `hexStringToByteArray`

```java
public byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
}
```

- Source: <https://stackoverflow.com/a/140861>
- Search term used: "java hex string to byte array"
- Example input string: "86eca66b50a2c1de3037bb619f2ec661" (capital letters and small letters both work)

### Java static fields are shared among subclasses

```java
class A {
    public static int a;
}

class B extends A {
    void plusOne() {
        a++;
    }
}

class C extends A {
    void plusOne() {
        a++;
    }
}

public class App {
    public static void main(String[] args) {
        B b = new B();
        b.plusOne();
        b.plusOne();

        C c = new C();
        c.plusOne();

        System.out.println(b.a);
        System.out.println(c.a);
    }
}
```

Result:
```txt
3
3
```

In order that different subclasses share different superclasses static field, see <https://stackoverflow.com/a/17172246> (potentially feasible solution).

### java.security.SecureRandom can generate results deterministically

```java
import java.security.SecureRandom;

public class App {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom(hexStringToByteArray("86eca66b50a2c1de3037bb619f2ec661"));
        int i = random.nextInt();
        System.out.println(i);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
```

The result is always "1627055688".

### Key pairs collision issue

A 512-bit input seed is supplied to generate a key pair. A question is, how many inputs are expected to find a collision of key pairs.

Suppose we have ![formula](<https://render.githubusercontent.com/render/math?math=M>) inputs, then there will be ![formula](<https://render.githubusercontent.com/render/math?math=\binom{M}{2}>) comparisons of pairs. There are ![formula](<https://render.githubusercontent.com/render/math?math=2^N>) different possible inputs.

We would like to find a collision, so we set ![formula](<https://render.githubusercontent.com/render/math?math=\binom{M}{2} = 2^N>).

Taking ![formula](<https://render.githubusercontent.com/render/math?math=\binom{M}{2} \approx M^2>), ![formula](<https://render.githubusercontent.com/render/math?math=\binom{M}{2} = 2^N \implies M^2 = 2^N \implies M = 2^{\frac{N}{2}}>).

Thus, ![formula](<https://render.githubusercontent.com/render/math?math=2^{\frac{512}{2}} \approx 1.16 \times 10^{77}\ \textrm{inputs}>) are expected to find a collision of key pairs. The number is enormous and is surely enough for our usage.

Source: COMP 3632 lecture 3 supplementary note
