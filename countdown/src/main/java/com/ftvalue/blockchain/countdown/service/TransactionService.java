package com.ftvalue.blockchain.countdown.service;

import com.ftvalue.blockchain.countdown.service.blockchain.BlockChainService;
import com.ftvalue.blockchain.countdown.utils.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by mylonelyplanet on 2018/7/25.
 */
@Slf4j(topic = "transaction")
@Service
public class TransactionService {

    @Autowired
    BlockChainService blockChainService;

    public String transferFrom(String from, String password, String to, String amount){
        final String address = AddressUtil.cleanAddress(from);
        return blockChainService.sendEtherTransaction(address,password,to,amount);
    }

    public String uploadData(String from, String password, String to, String data){
        final String address = AddressUtil.cleanAddress(from);
        return blockChainService.sendTextTransaction(address,password,to,data);
    }

}
