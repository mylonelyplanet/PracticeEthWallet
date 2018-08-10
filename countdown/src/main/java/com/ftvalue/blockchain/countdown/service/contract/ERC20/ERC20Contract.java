package com.ftvalue.blockchain.countdown.service.contract.ERC20;

import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;

@Slf4j
public class ERC20Contract extends Contract implements ERC20Token {


    protected ERC20Contract(String contractBinary, String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider gasProvider) {
        super(contractBinary, contractAddress, web3j, transactionManager, gasProvider);
    }

    private ERC20Contract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static ERC20Contract getContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ERC20Contract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Override
    public BigInteger totalSupply() {
        try {
            final Function function = new Function("totalSupply",
                    Arrays.<Type>asList(),
                    Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {
                    }));
            return executeRemoteCallSingleValueReturn(function, BigInteger.class).send();
        } catch (Exception e) {
            log.error("余额查询报错",e);
            return null;
        }
    }

    @Override
    public BigInteger balanceOf(String address) {
        try {
            final Function function = new Function("balanceOf",
                    Arrays.<Type>asList(new Address(address)),
                    Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {
                    }));
            return executeRemoteCallSingleValueReturn(function, BigInteger.class).send();
        } catch (Exception e) {
            log.error("余额查询报错",e);
            return null;
        }
    }

    @Override
    public Boolean transfer(String toAddress, BigInteger value) {
        try {
            final Function function = new Function("transfer",
                    Arrays.<Type>asList(new Address(toAddress),new Uint(value)),
                    Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                    }));
            return executeRemoteCallSingleValueReturn(function, Boolean.class).send();
        } catch (Exception e) {
            log.error("余额查询报错",e);
            return false;
        }
    }
}
