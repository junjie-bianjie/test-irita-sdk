# irita-sdk-java

irita-sdk-java for opb

## Build maven from local

```shell
    mvn install:install-file -Dfile=./src/main/resources/irita-sdk-java-0.1.jar -DgroupId=bianjie.ai -DartifactId=irita-sdk -Dversion=0.1-SNAPSHOT -Dpackaging=jar
```

## Key Manger

### 1 recover

#### 1.1 recover from mnemonic

```java
        String mnemonic="xxx";
        Key km=new KeyManager(mnemonic);
```

#### 1.2 recover from privKey

```java
        String privKeyHex="3c49175daf981965679bf88d2690e22144424e16c84e9d397ddb58b63603eeec";
        BigInteger privKey=new BigInteger(privKeyHex,16);
        Key km=new KeyManager(privKey);
```

#### 1.3 recover from keystore

**read from str**

```java
String keystore="-----BEGIN TENDERMINT PRIVATE KEY-----\n"+
        "salt: 183EF9B57DEF8EF8C3AD9D21DE672E1B\n"+
        "type: sm2\n"+
        "kdf: bcrypt\n"+
        "\n"+
        "cpreEPwi0X3yIdsAIf94fR6s8L1TnDAQd/r4ifID6GmQX5a+4ehMmnTp2JjDpUe5\n"+
        "kpgRI7CzF0DjKpPLvY9V9ZSXJFN42LHWscxqQ1E=\n"+
        "=nJvd\n"+
        "-----END TENDERMINT PRIVATE KEY-----";

        InputStream input=new ByteArrayInputStream(keystore.getBytes(StandardCharsets.UTF_8));
        Key km=new KeyManager(input,"123456");
```

**read from file**

```java
        FileInputStream input=new FileInputStream("src/test/resources/priv.key");
        Key km=new KeyManager(input,"123456");
```

#### 1.4 recover from CaKeystore

```java
        FileInputStream input=new FileInputStream("src/test/resources/ca.JKS");
        Key km=KeyManager.recoverFromCAKeystore(input,"123456");
```

### 2 export

```java
public interface Key {
    /**
     * export as keystore
     *
     * @param password password of keystore. The password is very important for recovery, so never forget it
     */
    String export(String password) throws IOException;
}
```

### 3 getPrivKey or getAddr

```java
public interface Key {
    BigInteger getPrivKey();

    String getAddr();
}
```

## How to use irita-sdk-java

```java
        FileInputStream input=new FileInputStream("src/test/resources/ca.JKS");
        Key km=KeyManager.recoverFromCAKeystore(input,"123456");

        int gas=10;
        int maxTxsBytes=1073741824;
        String mode="";
        double gasAdjustment=1.0;
        IritaClientOption.Fee fee=new IritaClientOption.Fee("13000000","uirita");
        IritaClientOption option=new IritaClientOption(gas,fee,maxTxsBytes,mode,gasAdjustment,km);

        String opbUri="https://opbningxia.bsngate.com:18602";
        String projectId="7b3c53beda5c48c6b07d98804e156389";
        String projectKey="7a3b5660c0ae47e2be4f309050c1d304";
        String chainId="wenchangchain";
        OpbOption opbOption=new OpbOption(opbUri,projectId,projectKey);
        client=new IritaClient(chainId,opbOption,option);
        wasmClient=client.getWasmClient();
        comGovClient=client.getCommunityGovClient();
        baseTx=comGovClient.getComGovBaseTx();

        ContractAddress.DEFAULT="your ContractAddress";
```

## Use CommunityGovClient

### 1. add department(添加部门管理员)

```java
        final String publicKey="iaa1ytemz2xqq2s73ut3ys8mcd6zca2564a5lfhtm3";
final String department="测试部门";

        try{
        comGovClient.addDepartment(department,publicKey,baseTx);
        }catch(ContractException e){
        // you can use log to record
        e.printStackTrace();
        }
```

### 2. add a member(添加一个成员)

```java
        String newAddr="iaa1wfs050mv8taydn4cttsrhr5dq3tpdaemcm5sk2";

        try{
        comGovClient.addMember(newAddr,Role.HASH_ADMIN,baseTx);
        }catch(ContractException|IOException e){
        e.printStackTrace();
        }
// 关于角色见 Role.java
```

### 3. other operation(其他方法)

详见KeyMangerTest.java, WasmTest.java, OpbTest.java
