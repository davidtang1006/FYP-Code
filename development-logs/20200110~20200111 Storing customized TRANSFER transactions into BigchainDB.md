# 20200110~20200111 Storing customized TRANSFER transactions into BigchainDB

## Introduction

### Instructions to evaluate my code

- Start [Docker](https://docs.docker.com/compose/install/).
- Execute `make reset; make run;` using Git Bash under "bigchaindb-server" folder every time before running driver code.
- In Eclipse, go to "com.helperlinker.bigchaindb >> Driver.java" then got to "Run >> Run Configurations... >> Arguments". Include `-Dorg.slf4j.simpleLogger.defaultLogLevel=debug` in "VM arguments" field.
- Run AppDriver.java.

### Example usage

```txt
[main] INFO com.bigchaindb.builders.BigchainDbConfigBuilder - Connected to node - http://localhost:9984/ after 0 retry(s)...
(*) Keys Generated
GfHq2tTVk9z4eXgyLwyYQMWFDhkvHdrqJsHYwt2SRBshANpXZWNzzgMfbwDq
2mWNaEKEJrtB1rEvHsFx8aH5FbEkKtAQyFwqwRw6678QZtSAdDHg4nCx5dKdyQq1nD
(*) Assets Prepared
(*) Metadata Prepared
[main] DEBUG com.bigchaindb.api.TransactionsApi - sendTransaction Call :{"asset":{"data":{"hashedPwd":"b8db2e4680c00f5075682be1179797bf29f15f7a2b87a6818eec3cf9e47955ff","idCardNum":"A1234","salt":"a0eb205f1dfcc32fbb21c4febb8a4669","userType":"HELPER"}},"id":"4c067bbca0dd331a054b06b2a676236e589700c19c3d3289f76370af5ead8b59","inputs":[{"fulfillment":"pGSAIGKEvj548ymz2hhC51RiKiD4YNKI_o4jwHoBpimvSBvwgUB2GhY45wpz-eoSP-FCZ8o6zFBbz8syuOS0qgqQqhUnmZXoxzcCj4u4jTKm13n2RN05hK2jHYgYFtbc6PYevAYF","fulfills":null,"owners_before":["7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV"]}],"metadata":{"selfIntro":""},"operation":"CREATE","outputs":[{"amount":"1","condition":{"details":{"public_key":"7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV","type":"ed25519-sha-256"},"uri":"ni:///sha-256;4w8V_cQI_AeUhsFzzCZPgr_snwzYb_ucaGb1UKZbwt4?fpt=ed25519-sha-256&cost=131072"},"public_keys":["7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV"]}],"version":"2.0"}
(*) CREATE Transaction sent - 4c067bbca0dd331a054b06b2a676236e589700c19c3d3289f76370af5ead8b59
pushedSuccessfully
Transaction posted successfully

(*) Keys Generated
GfHq2tTVk9z4eXgyH7TwH6e8bMpAxdTYddv9EEmYKzPgSbe7dgmMr38tMKc6
2mWNaEKEJrtB1rEvHsFx8aKeptg1bM9sBmE1Ea11159apuiuiVgyyUXtBUUVBMxNue
(*) Assets Prepared
(*) Metadata Prepared
[main] DEBUG com.bigchaindb.api.TransactionsApi - sendTransaction Call :{"asset":{"data":{"hashedPwd":"0f2217e5473b6caf78c9589a71640f15bf09de0f8d54bca369a677d89d9623f7","idCardNum":"B1234","salt":"45222d65d019849bc683cdd34e9b025b","userType":"HELPER"}},"id":"1e665d3f288183727dbe4ab09ee7008c14552ee650ece8efa867e712ead0259d","inputs":[{"fulfillment":"pGSAICmEVA9-5wX_nYqKPaGlIA3NQzFiMFHeNhLRxH6fgAgrgUCfkcIC_0akrQszxtpdiM2j6nzWgrQgTsbYrCzvROutMj_iXKV_tvLoOBYR-Dq7y4FnuwDXjQZrnLRL-TNKeGQL","fulfills":null,"owners_before":["3o4kMXgtnJFJJ9RWMf6mbJoq5UAmqMb7XupGqY9Y7qHk"]}],"metadata":{"selfIntro":""},"operation":"CREATE","outputs":[{"amount":"1","condition":{"details":{"public_key":"3o4kMXgtnJFJJ9RWMf6mbJoq5UAmqMb7XupGqY9Y7qHk","type":"ed25519-sha-256"},"uri":"ni:///sha-256;Q1Av3iq-7idrw5xteZcI0gsrUUs-cuNy6fSgVyNIPYQ?fpt=ed25519-sha-256&cost=131072"},"public_keys":["3o4kMXgtnJFJJ9RWMf6mbJoq5UAmqMb7XupGqY9Y7qHk"]}],"version":"2.0"}
(*) CREATE Transaction sent - 1e665d3f288183727dbe4ab09ee7008c14552ee650ece8efa867e712ead0259d
pushedSuccessfully
Transaction posted successfully

(*) Keys Generated
GfHq2tTVk9z4eXgyNPxp992vLT5LtjEQGyZQBwEZcxSvqGCRHUExycXFboCq
2mWNaEKEJrtB1rEvHsFx8aRpvZ7F4Lu2o5BK2pVkzb3mvDA6NBBUnev1tambo2Hgtv
(*) Assets Prepared
(*) Metadata Prepared
[main] DEBUG com.bigchaindb.api.TransactionsApi - sendTransaction Call :{"asset":{"data":{"hashedPwd":"a4a359707ee7743bf2d56d95f1678fc2e5e9cc9450607a49eb20e1414887a62c","idCardNum":"C1234","salt":"c08231a23b32ee95b895e5e58f5f743a","userType":"HELPER"}},"id":"e448455d50ef303195c1236d669ea63030cbdc65abf6d0357aca8901c04b0ed4","inputs":[{"fulfillment":"pGSAIHgItQtMIn7FzZcY7pXIlkPh3cnHvd-Yes0dRL7dTZPWgUD7EDck2U1VufFZ3TK4PAGZEYmuexO1x_yF-tB6kTrtlb-T32IMNg2MBSL-QE_w5edF_RHxu15rXB3_YEA3S5wK","fulfills":null,"owners_before":["95ZdDa5gXPWUEFCMzzk2Z1GrNSE2E29RBhHsy7XuNJtV"]}],"metadata":{"selfIntro":""},"operation":"CREATE","outputs":[{"amount":"1","condition":{"details":{"public_key":"95ZdDa5gXPWUEFCMzzk2Z1GrNSE2E29RBhHsy7XuNJtV","type":"ed25519-sha-256"},"uri":"ni:///sha-256;W4fTmnpvDfu69RYJ3g2vd9Mk2ndpJ68o9sCz7bLMmPk?fpt=ed25519-sha-256&cost=131072"},"public_keys":["95ZdDa5gXPWUEFCMzzk2Z1GrNSE2E29RBhHsy7XuNJtV"]}],"version":"2.0"}
(*) CREATE Transaction sent - e448455d50ef303195c1236d669ea63030cbdc65abf6d0357aca8901c04b0ed4
pushedSuccessfully
Transaction posted successfully

Please select an option.
1. I am a helper.
2. I am an employer.
1

Enter your ID card number (enter nothing to exit): A1234
Enter your password (enter nothing to exit): aaa

Please select an option.
1. Create/modify your profile for employment.
0. Log out.
1

Self-introduction: a0 a1 a2
Do you want to save your profile? [Y/N] Y
(*) Transfer Metadata Prepared
[main] DEBUG com.bigchaindb.api.TransactionsApi - sendTransaction Call :{"asset":{"id":"4c067bbca0dd331a054b06b2a676236e589700c19c3d3289f76370af5ead8b59"},"id":"00c3e2bfee843b9500f301f2dca02c0b4dc88515593023be9e8bafedb91d0864","inputs":[{"fulfillment":"pGSAIGKEvj548ymz2hhC51RiKiD4YNKI_o4jwHoBpimvSBvwgUCVycXcm-6nBnDQkeD5PXsFtOz3aImbFjYikIlHH7FVkbKgxK146X2p3Egr1-Wr20zPTn8luiZEpCsGWIL7DsQB","fulfills":{"output_index":0,"transaction_id":"4c067bbca0dd331a054b06b2a676236e589700c19c3d3289f76370af5ead8b59"},"owners_before":["7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV"]}],"metadata":{"selfIntro":"a0 a1 a2"},"operation":"TRANSFER","outputs":[{"amount":"1","condition":{"details":{"public_key":"7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV","type":"ed25519-sha-256"},"uri":"ni:///sha-256;4w8V_cQI_AeUhsFzzCZPgr_snwzYb_ucaGb1UKZbwt4?fpt=ed25519-sha-256&cost=131072"},"public_keys":["7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV"]}],"version":"2.0"}
(*) TRANSFER Transaction sent - 00c3e2bfee843b9500f301f2dca02c0b4dc88515593023be9e8bafedb91d0864
pushedSuccessfully
Transaction posted successfully

Please select an option.
1. Create/modify your profile for employment.
0. Log out.
1

Self-introduction: a3 a4 a5
Do you want to save your profile? [Y/N] Y
(*) Transfer Metadata Prepared
[main] DEBUG com.bigchaindb.api.TransactionsApi - sendTransaction Call :{"asset":{"id":"4c067bbca0dd331a054b06b2a676236e589700c19c3d3289f76370af5ead8b59"},"id":"26a1d2fb981c95eaa932bb6ec3032a9e4287c7a506c0430c0a81a264fb3358c7","inputs":[{"fulfillment":"pGSAIGKEvj548ymz2hhC51RiKiD4YNKI_o4jwHoBpimvSBvwgUAhbwwOybqZcHzjJty9oq9sAYXr2KiKCGAY2Te_IYjNUXcNEHQXObAtQsUu42PrpJ45PZTNr4lOCswrcQcWVZMC","fulfills":{"output_index":0,"transaction_id":"00c3e2bfee843b9500f301f2dca02c0b4dc88515593023be9e8bafedb91d0864"},"owners_before":["7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV"]}],"metadata":{"selfIntro":"a3 a4 a5"},"operation":"TRANSFER","outputs":[{"amount":"1","condition":{"details":{"public_key":"7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV","type":"ed25519-sha-256"},"uri":"ni:///sha-256;4w8V_cQI_AeUhsFzzCZPgr_snwzYb_ucaGb1UKZbwt4?fpt=ed25519-sha-256&cost=131072"},"public_keys":["7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV"]}],"version":"2.0"}
(*) TRANSFER Transaction sent - 26a1d2fb981c95eaa932bb6ec3032a9e4287c7a506c0430c0a81a264fb3358c7
pushedSuccessfully
Transaction posted successfully

Please select an option.
1. Create/modify your profile for employment.
0. Log out.
1

Self-introduction: a6 a7 a8
Do you want to save your profile? [Y/N] Y
(*) Transfer Metadata Prepared
[main] DEBUG com.bigchaindb.api.TransactionsApi - sendTransaction Call :{"asset":{"id":"4c067bbca0dd331a054b06b2a676236e589700c19c3d3289f76370af5ead8b59"},"id":"49daccd317ab9acb348cb611fa894462a219d7451cc4b928cbf71f00c7b8f990","inputs":[{"fulfillment":"pGSAIGKEvj548ymz2hhC51RiKiD4YNKI_o4jwHoBpimvSBvwgUBPnYNJe8kmFdUzzmcbc1RsoqUIwiU8ge1gERaHPc3BGFbiX9pVBPuIVdHS97KeGnBKtIhhbwl5yDdfJfGfVf4B","fulfills":{"output_index":0,"transaction_id":"26a1d2fb981c95eaa932bb6ec3032a9e4287c7a506c0430c0a81a264fb3358c7"},"owners_before":["7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV"]}],"metadata":{"selfIntro":"a6 a7 a8"},"operation":"TRANSFER","outputs":[{"amount":"1","condition":{"details":{"public_key":"7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV","type":"ed25519-sha-256"},"uri":"ni:///sha-256;4w8V_cQI_AeUhsFzzCZPgr_snwzYb_ucaGb1UKZbwt4?fpt=ed25519-sha-256&cost=131072"},"public_keys":["7daMUnZ1QeC3d9po2tUBJx4jAfenZ8mXTjRuzBNKNSuV"]}],"version":"2.0"}
(*) TRANSFER Transaction sent - 49daccd317ab9acb348cb611fa894462a219d7451cc4b928cbf71f00c7b8f990
pushedSuccessfully
Transaction posted successfully
```

## Implementation

### Hiding the password input

```java
import java.io.Console;

public class App {
    public static void main(String[] args) {
        Console cnsl = null;
        String string = null;
        try {
            // Create a console object
            cnsl = System.console();
            if (cnsl != null) {
                // Read line from the user input
                string = cnsl.readLine("Name: ");
                System.out.println("Name is: " + string);

                // read password into the char array
                char[] pwd = cnsl.readPassword("Password: ");
                System.out.println("Password is: " + String.valueOf(pwd));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

- Source: <https://www.tutorialspoint.com/java/io/console_readpassword.htm>
- Search term used: "java console readpassword"

The code does not work in Eclipse console, neither does Git Bash, but in Windows Command Prompt. The solution is not adopted.

### Summary

A helper can now provide and update personal information (a self-introduction), which is stored in BigchainDB. This is done by performing a TRANSFER operation. The asset representing the helper is transferred back to himself/herself with metadata, which contains content of personal information, updated.

Users classes were modified. Variables were encapsulated in classes for easier management.

Also, the actions in AppDriver.java was made cancellable.
