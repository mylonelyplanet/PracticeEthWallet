package com.ftvalue.blockchain.countdown.service.blockchain;

import com.ftvalue.blockchain.countdown.keystore.Keystore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tx.RawTransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

/**
 * Created by mylonelyplanet on 2018/7/25.
 */
@Slf4j(topic = "Ethereum")
@Component
public class EthereumService implements BlockChainService {

    private static final BigInteger gasLimit = BigInteger.valueOf(31_000L);


    @Autowired
    Web3j web3j;

    @Autowired
    Keystore keystore;

    @Override
    public BigInteger getBlockNumber() throws RuntimeException {
        try{
            EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().sendAsync().get();
            return ethBlockNumber.getBlockNumber();
        }catch (InterruptedException | ExecutionException e){
            throw new RuntimeException("get ETH block number failed");
        }
    }

    /***
     * 发送在线金额交易
     * @return transaction hash
     * @throws RuntimeException
     */
    @Override
    public String sendEtherTransaction(String from, String password,String to, String amount) throws RuntimeException {

        try{
            String fromAddress = "0x"+from;
            String toAddress = "0x"+to;

            //get 最新gasPrice 及时到账
            BigInteger gasPrice = getGasPrice();
            log.info("[sendRawTransaction][step 1] get gas price:{}",gasPrice);
            //get credentials
            Credentials credentials = keystore.loadStoredKey(from,password);

            //getNonce
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                    fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            log.info("[sendRawTransaction][step 2] get transaction nonce:{}",nonce);

            //创建交易
            BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                                     nonce, gasPrice, gasLimit, toAddress, value);
            log.info("[sendRawTransaction][step 3] build raw transaction:{}",rawTransaction.getData());

            //签名Transaction
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            log.info("[sendRawTransaction][step 4] sign transaction:{}",hexValue);

            //发送交易
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String transactionHash = ethSendTransaction.getTransactionHash();
            log.info("[sendRawTransaction][step 5] send transaction:{}",transactionHash);

            return transactionHash;
        }catch (InterruptedException | ExecutionException  e){
            throw new RuntimeException("sendEtherTransaction failed");
        }
    }

    /***
     * 发送备注上链，0eth
     * @return transaction hash
     * @throws RuntimeException
     */
    @Override
    public String sendTextTransaction(String from, String password,String to, String data) throws RuntimeException {

        try{

            String fromAddress = "0x"+from;
            String toAddress = "0x"+to;

            //get 最新gasPrice 及时到账
            BigInteger gasPrice = getGasPrice();
            log.info("[sendRawTransaction][step 1] get gas price:{}",gasPrice);
            //get credentials
            Credentials credentials = keystore.loadStoredKey(from,password);

            RawTransactionManager rawTransactionManager = new RawTransactionManager(web3j,credentials);

            EthSendTransaction ethSendTransaction = rawTransactionManager.sendTransaction(gasPrice,gasLimit,toAddress,data,BigInteger.ZERO);

            String transactionHash =  ethSendTransaction.getTransactionHash();
//            //getNonce
//            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
//                    fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
//            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
//            log.info("[sendRawTransaction][step 2] get transaction nonce:{}",nonce);
//
//            //创建交易
//            RawTransaction rawTransaction = RawTransaction.createTransaction(
//                    nonce, gasPrice, gasLimit, toAddress,BigInteger.ONE, Numeric.toHexStringNoPrefixZeroPadded(Numeric.toBigInt(data), 64));
//            log.info("[sendRawTransaction][step 3] build raw transaction with data:{}",rawTransaction.getData());
//
//            //签名Transaction
//            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//            String hexValue = Numeric.toHexString(signedMessage);
//            log.info("[sendRawTransaction][step 4] sign transaction:{}",hexValue);
//            //发送交易
//            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
//            String transactionHash = ethSendTransaction.getTransactionHash();
            log.info("[sendRawTransaction][step 5] send transaction:{}",transactionHash);

            return transactionHash;
        }catch (IOException e){
            throw new RuntimeException("sendTextTransaction failed");
        }
    }

    @Override
    public BigInteger getGasPrice() throws RuntimeException{

        try{
            EthGasPrice ethGasPrice = web3j.ethGasPrice().sendAsync().get();
            return ethGasPrice.getGasPrice();
        }catch (InterruptedException | ExecutionException e){
            throw new RuntimeException("get ETH GAS Price failed");
        }
    }

    @Override
    public BigInteger getBalance(String address) throws RuntimeException{

        try{
            String zxAddress = "0x" + address;
            EthGetBalance ethGetBalance = web3j.ethGetBalance(zxAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
            return ethGetBalance.getBalance();
        }catch (InterruptedException | ExecutionException e){
            throw new RuntimeException("get ETH Balance failed,"+address);
        }
    }
}
