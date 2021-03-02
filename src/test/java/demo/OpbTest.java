package demo;

import irita.opb.OpbOption;
import irita.sdk.client.IritaClient;
import irita.sdk.client.IritaClientOption;
import irita.sdk.constant.ContractAddress;
import irita.sdk.constant.enums.Role;
import irita.sdk.exception.ContractException;
import irita.sdk.module.base.Account;
import irita.sdk.module.base.BaseTx;
import irita.sdk.module.community_gov.CommunityGovClient;
import irita.sdk.module.keys.Key;
import irita.sdk.module.keys.KeyManager;
import irita.sdk.module.wasm.WasmClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OpbTest {
    private IritaClient client;
    private WasmClient wasmClient;
    private CommunityGovClient comGovClient;
    private BaseTx baseTx;

    @BeforeEach
    public void init() throws Exception {
        FileInputStream input = new FileInputStream("src/test/resources/ca.JKS");
        Key km = KeyManager.recoverFromCAKeystore(input, "123456");

        int gas = 10;
        int maxTxsBytes = 1073741824;
        String mode = "";
        double gasAdjustment = 1.0;
        IritaClientOption.Fee fee = new IritaClientOption.Fee("13000000", "uirita");
        IritaClientOption option = new IritaClientOption(gas, fee, maxTxsBytes, mode, gasAdjustment, km);

        String opbUri = "https://opbningxia.bsngate.com:18602";
        String projectId = "7b3c53beda5c48c6b07d98804e156389";
        String projectKey = "xxxTODO";
        String chainId = "wenchangchain";
        OpbOption opbOption = new OpbOption(opbUri, projectId, projectKey);
        client = new IritaClient(chainId, opbOption, option);
        wasmClient = client.getWasmClient();
        comGovClient = client.getCommunityGovClient();
        baseTx = comGovClient.getComGovBaseTx();

        ContractAddress.DEFAULT = "your ContractAddress";
    }

    @Test
    public void queryAccountNumber() {
        Account account = client.queryAccount("iaa1wfs050mv8taydn4cttsrhr5dq3tpdaemcm5sk2");
        System.out.println(account.getAccountNumber());
        assertTrue(account.getAccountNumber() > 0);
    }

    @Test
    public void addDepartment() {
        // publicKey == address in this version
        final String publicKey = "iaa1ytemz2xqq2s73ut3ys8mcd6zca2564a5lfhtm3";
        final String department = "测试部门";

        try {
            comGovClient.addDepartment(department, publicKey, baseTx);
        } catch (ContractException e) {
            // you can use log to record
            e.printStackTrace();
        }

        // query contract for valid
        Map<String, String> map = wasmClient.exportContractState(ContractAddress.DEFAULT);

        String v1 = map.get(department);
        assertEquals("{}", v1);
        String v2 = map.get("iaa1ytemz2xqq2s73ut3ys8mcd6zca2564a5lfhtm3");
        assertEquals("{\"department_name\":\"测试部门\",\"role\":0,\"public_key\":\"iaa1ytemz2xqq2s73ut3ys8mcd6zca2564a5lfhtm3\"}", v2);
    }

    @Test
    public void addMember() {
        String newAddr = "iaa1wfs050mv8taydn4cttsrhr5dq3tpdaemcm5sk2";

        try {
            comGovClient.addMember(newAddr, Role.HASH_ADMIN, baseTx);
        } catch (ContractException | IOException e) {
            e.printStackTrace();
        }

        // verify from db
        Map<String, String> map = wasmClient.exportContractState(ContractAddress.DEFAULT);
        assertNotNull(map.get(newAddr));
    }

    @Test
    public void exportContractState() {
        Map<String, String> map = wasmClient.exportContractState(ContractAddress.DEFAULT);
        System.out.println(map);
    }

    @Test
    public void getHash() {
        String strHash = "123";
        String fileHash = "789";
        boolean isExisted = false;

        isExisted = comGovClient.getHash(strHash);
        assertTrue(isExisted);

        isExisted = comGovClient.getHash(fileHash);
        assertTrue(isExisted);
    }
}
