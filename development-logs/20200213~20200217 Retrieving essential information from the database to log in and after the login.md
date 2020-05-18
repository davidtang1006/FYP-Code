# 20200213~20200217 Retrieving essential information from the database to log in and after the login

## Introduction

### Instructions to evaluate my code

Start [Docker](https://docs.docker.com/compose/install/). Execute `make reset; make run;` using Git Bash under "bigchaindb-server" folder for the first time before running driver code. One can simply execute `make run` afterwards.

If .project is missing, go to the root directory and generate an Eclipse project using Maven by `mvn eclipse:eclipse`. Or, import the directory as a Maven project. Use JDK 11.

To use JDK 11,
- Right click the project.
- Go to "Java Build Path >> Libraries".
- Add JRE System Library (version 11) and delete any other JRE System Libraries.
- Go to "Properties >> Java Compiler".
- Select "11" for the complier compliance level.

Run AppDriver.java.

### Example usage

1. Execute `make reset; make run;` using Git Bash under "bigchaindb-server" folder. Run AppDriver.java.
2. Log in as the helper "A1234", update three times. Log in as the helper "B1234", update once. (Copy and paste the following lines into Eclipse console.)
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
3. Try to log in as the helper "A1234" with a wrong password "abc", which should not work.
4. Try to log in as the helper "D1234" (which is actually an employer's ID card number) with the password "aaa", which should not work.
5. Try to log in as the helper "Z1234" (which does not exist) with the password "aaa", which should not work.
6. Try keywords "a0 a1" (log in as an employer). No results should be returned since the corresponding metadata is not the latest.
7. Try keywords "a6 a7" and "a8 a9". For the keywords "a6 a7", the result of "A1234" should appear before "B1234". For the keywords "a8 a9", the result of "B1234" should appear before "A1234".
8. Log in as the helper "A1234", update once. Log in as the helper "C1234", update once. (Copy and paste the following lines into Eclipse console.)
   ```txt
   1
   A1234
   aaa
   1
   a10 a11 a12
   Y
   0
   1
   C1234
   aaa
   1
   a10 a11 a12
   Y
   0

   ```

## Implementation

### A quick test of Java scopes

```java
class A {
    int a;

    public A(int a) {
        this.a = a;
    }
}

public class App {
    public static void main(String[] args) {
        int i = 0;
        if (i == 0) {
            A a = new A(0);

            System.out.println(a.a); // This works

            String s0 = new String("0");
            String s1 = "0";

            System.out.println(s0); // This works
            System.out.println(s1); // This works
        }
        System.out.println(a.a); // This does not work

        System.out.println(s0); // This does not work
        System.out.println(s1); // This does not work
    }
}
```

### Getting values from an embedded document

Dot notation does not work. See <https://stackoverflow.com/questions/35011625>.

### `Random` and `SecureRandom` class

We have the following code for testing:
```java
import java.security.SecureRandom;
import java.util.Random;

public class App {
    public static void main(String[] args) {
        SecureRandom random1 = new SecureRandom(hexStringToByteArray("86eca66b50a2c1de3037bb619f2ec661"));
        int i = random1.nextInt();
        System.out.println(i);
        i = random1.nextInt();
        System.out.println(i);
        i = random1.nextInt();
        System.out.println(i);
        System.out.println();

        random1 = new SecureRandom(hexStringToByteArray("86eca66b50a2c1de3037bb619f2ec661"));
        i = random1.nextInt();
        System.out.println(i);
        i = random1.nextInt();
        System.out.println(i);
        i = random1.nextInt();
        System.out.println(i);
        System.out.println();

        Random random2 = new Random(20);
        i = random2.nextInt();
        System.out.println(i);
        i = random2.nextInt();
        System.out.println(i);
        i = random2.nextInt();
        System.out.println(i);
        System.out.println();

        random2 = new Random(20);
        i = random2.nextInt();
        System.out.println(i);
        i = random2.nextInt();
        System.out.println(i);
        i = random2.nextInt();
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

The result of execution 1 with JDK 11.0.5:
```txt
1627055688
454542455
333519441

105059482
-1444595062
141898296

-1150867590
-1704868423
884779003

-1150867590
-1704868423
884779003
```

The result of execution 2 with JDK 11.0.5:
```txt
1627055688
454542455
333519441

105059482
-1444595062
141898296

-1150867590
-1704868423
884779003

-1150867590
-1704868423
884779003
```

Even after "re-initializing" with the same seed, a `SecureRandom` object does not produce the same result. However, with JDK 11.0.5, different executions yield the same random series.

The result of execution 1 with <https://www.onlinegdb.com/>:
```txt
1627293594
259957378
1015543383

-2146931601
-1456576034
-636827118

-1150867590
-1704868423
884779003

-1150867590
-1704868423
884779003
```

The result of execution 2 with <https://www.onlinegdb.com/>:
```txt
-581304511
1563641731
294076583

546097315
2132293268
-1426670930

-1150867590
-1704868423
884779003

-1150867590
-1704868423
884779003
```

A different behaviour of `SecureRandom` object is observed. In this case, different executions yield different random series. Yet, we can see that `Random` class always produces the same random series for different executions. Also, `Random` class can be re-initialized.

### Using `Random` instead of `SecureRandom`

It turns out that `SecureRandom` cannot be re-initialized properly such that key pairs cannot be generated properly. Therefore, `Random` is used instead of `SecureRandom`. I return to the solution from 45d2a01d8a951d4642f629874e77baffd93cacf2.

However, one `Random` object is slightly not enough. `Random` class takes `long` as the seed. ![formula](<https://render.githubusercontent.com/render/math?math=2^{\frac{64}{2}} \approx 4.29 \ \textrm{billion}\ \textrm{inputs}>) are expected to find a collision of key pairs (see <https://github.com/davidtang1006/FYP-Code/issues/39>). I want the expected number of inputs to find a collision to be higher, so I use two `Random` objects such that the expected number of inputs becomes ![formula](<https://render.githubusercontent.com/render/math?math=2^{\frac{64 \cdot 2}{2}} \approx 1.84 \times 10^{19}\ \textrm{inputs}>).
