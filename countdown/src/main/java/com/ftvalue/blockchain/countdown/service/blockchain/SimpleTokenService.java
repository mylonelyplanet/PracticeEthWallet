package com.ftvalue.blockchain.countdown.service.blockchain;

import com.ftvalue.blockchain.countdown.keystore.Keystore;
import com.ftvalue.blockchain.countdown.service.contract.SimpleTokenContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class SimpleTokenService {
    @Autowired
    Web3j web3j;

    @Autowired
    Keystore keystore;

    public BigInteger getBalance(String address, String password) throws Exception {

        try{

            String zxAddress = "0x" + address;

            /*EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt("0x420be0e1f18b33b757291140af1ab96534fe32c1af9851068f39ae1ec035c5f0").send();

            String contractAddress = transactionReceipt.getTransactionReceipt().get().getContractAddress();*/
            String contractAddress = "0x55923d84ad63c1607cd6378d320ac7388c7c957d";
            //log.info("contractAddress: "+contractAddress);

            //String contractAddress = "0x38E374700e008bEc3ff46B0d966E765aAaa43545";

            //get credentials
            Credentials credentials = keystore.loadStoredKey(address,password);

            SimpleTokenContract contract = SimpleTokenContract.load(contractAddress, web3j, credentials, BigInteger.valueOf(20000000000l), BigInteger.valueOf(6721975));
            String contractAddress1 = contract.getContractAddress();
            log.info("contractAddress1: "+contractAddress1);

            log.info("contract.isValid: "+contract.isValid());

            String hello = contract.hello().send();
            log.info("msg: " + hello);

            String msg = contract.helloY("asas").send();
            log.info("msg2: " + msg);

            BigInteger balance = contract.getBalance(zxAddress).send();
            log.info("balance: " + balance);

            EthGetBalance ethGetBalance = web3j.ethGetBalance(zxAddress, DefaultBlockParameterName.LATEST).sendAsync().get();

            return ethGetBalance.getBalance();
        }catch (InterruptedException | ExecutionException e){
            throw new RuntimeException("get ETH Balance failed,"+address);
        }
    }
}
