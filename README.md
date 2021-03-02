# irita-sdk-java

irita-sdk-java for opb

## Build maven from local

```shell
    mvn install:install-file -Dfile=./src/main/resources/irita-sdk-java-0.1.jar -DgroupId=bianjie.ai -DartifactId=irita-sdk -Dversion=0.1-SNAPSHOT -Dpackaging=jar
```

## Key Manger

### 1 recover

####  recover from CaKeystore

```java
        FileInputStream input=new FileInputStream("src/test/resources/ca.JKS");
        Key km=KeyManager.recoverFromCAKeystore(input,"xxx");
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

        String opbUri="xxx";
        String projectId="xxx";
        String projectKey="xxx";
        String chainId="xxx";
        OpbOption opbOption=new OpbOption(opbUri,projectId,projectKey);
        client=new IritaClient(chainId,opbOption,option);
        wasmClient=client.getWasmClient();
        comGovClient=client.getCommunityGovClient();
        baseTx=comGovClient.getComGovBaseTx();

        ContractAddress.DEFAULT="xxx";
```

## Use CommunityGovClient

### 1. add department(添加部门管理员)

```java
        final String publicKey="xxx";
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
        String newAddr="xxx";

        try{
        comGovClient.addMember(newAddr,Role.HASH_ADMIN,baseTx);
        }catch(ContractException|IOException e){
        e.printStackTrace();
        }
// 关于角色见 Role.java
```

### 3. other operation(其他方法)

详见KeyMangerTest.java, WasmTest.java, OpbTest.java
