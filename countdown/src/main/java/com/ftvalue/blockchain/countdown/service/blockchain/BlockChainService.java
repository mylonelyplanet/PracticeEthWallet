package com.ftvalue.blockchain.countdown.service.blockchain;

import java.math.BigInteger;

/**
 * Created by mylonelyplanet on 2018/7/25.
 */
public interface BlockChainService {

    BigInteger getBlockNumber() throws RuntimeException;

    BigInteger getGasPrice();

    BigInteger getBalance(String address);

    String sendEtherTransaction(String from, String password,String to, String amount) throws RuntimeException;

    String sendTextTransaction(String from, String password,String to, String data) throws RuntimeException;


}
