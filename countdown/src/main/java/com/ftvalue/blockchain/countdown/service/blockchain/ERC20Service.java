package com.ftvalue.blockchain.countdown.service.blockchain;

import com.ftvalue.blockchain.countdown.keystore.Keystore;
import com.ftvalue.blockchain.countdown.service.contract.ERC20.ERC20Contract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;

@Service("ERC20Service")
@Slf4j
public class ERC20Service implements BlockChainService{
    @Autowired
    Web3j web3j;

    @Autowired
    Keystore keystore;

    private ERC20Contract contract;

    public void init(String account,String password,String contractAddress, BigInteger gasPrice, BigInteger gasLimit){
        Credentials credentials = keystore.loadStoredKey(account,password);
        contract = ERC20Contract.getContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Override
    public BigInteger getBlockNumber() throws RuntimeException {
        if(checkInit())return null;
        return contract.getTransactionReceipt().get().getBlockNumber();
    }

    @Override
    public BigInteger getGasPrice() {
        if(checkInit())return null;
        return contract.getGasPrice();
    }

    @Override
    public BigInteger getBalance(String address) {
        if(checkInit())return null;
        return contract.balanceOf(address);
    }

    @Override
    public String sendEtherTransaction(String from, String password, String to, String amount) throws RuntimeException {
        return null;
    }

    @Override
    public String sendTextTransaction(String from, String password, String to, String data) throws RuntimeException {
        return null;
    }

    private boolean checkInit() {
        if(contract == null){
            log.error("please init service");
        }
        return contract != null;
    }
}
